package com.example.service.implement;

import com.example.Entity.Cart;
import com.example.Entity.Product;
import com.example.Entity.Size;
import com.example.Entity.User;
import com.example.exception.CustomException;
import com.example.mapper.CartMapper;
import com.example.repository.CartRepository;
import com.example.request.CartRequest;
import com.example.response.CartItemResponse;
import com.example.response.CartResponse;
import com.example.response.Response;
import com.example.service.CartService;
import com.example.service.ProductService;
import com.example.service.UserService;
import com.example.util.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ProductService productService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private MethodUtils methodUtils;

    private CartItemResponse convertCartToCartItemResponse(Cart cart) {
        CartItemResponse cartItemResponse = cartMapper.cartToCartItemResponse(cart);
        cartItemResponse.setProduct(cart.getProduct());
        return cartItemResponse;
    }

    // kiểm tra size request có tồn tại trong product không và số lượng của size có > 0 hay không
    private Size validateSizeInProduct(Product product, int sizeName) throws CustomException {
        Size sizeExist = product.getSizes().stream()
                .filter(s -> Objects.equals(s.getName(), sizeName))
                .findFirst()
                .orElseThrow(() -> new CustomException(
                        "Product not have size:" + sizeName + " !!!",
                        HttpStatus.NOT_FOUND.value()
                ));

        // kiểm tra xem số lượng của size request trong kho > 0 hay không
        if (sizeExist.getQuantity() == 0) {
            throw new CustomException(
                    "Product wih size : " + sizeName + " is out of stock !!!",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        return sizeExist;
    }

    // kiểm tra tính hợp lệ của quantity trong cart item
    private void validateQuantity(int quantity, int maxQuantity, int stockQuantity) throws CustomException {
        if (quantity > stockQuantity) {
            throw new CustomException(
                    "You can only add a maximum of " + stockQuantity + " products to your cart !!!",
                    HttpStatus.BAD_REQUEST.value()
            );
        } else if (quantity > maxQuantity) {
            throw new CustomException(
                    "You can only add to cart " + maxQuantity + " products !!!",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }

    @Override
    public Cart getById(Long id) throws CustomException {
        Cart cartItem = cartRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Cart item not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        return cartItem;
    }

    @Override
    @Transactional
    public CartItemResponse addToCart(CartRequest cartRequest) throws CustomException {
        String emailUser = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(emailUser);

        int totalCartItem = cartRepository.countByUserId(user.getId());

        // Kiểm ra số lượng đã có trong giỏ hàng của user
        if (totalCartItem > 50) {
            throw new CustomException(
                    "Your shopping cart is full, please delete some cart items !!!",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        // lấy ra information của product request
        Product product = productService.getById(cartRequest.getProductId());

        // kiểm tra xem size request có tồn tại trong product request đó không và số lượng của size đó trong kho
        Size size = this.validateSizeInProduct(product, cartRequest.getSize());

        // Kiểm tra xem với sản phẩm đó và size đó đã tồn tại trong db của user đó chưa (nếu tồn tại => update, ngược lại => create)
        Optional<Cart> cartItemExist = cartRepository.findByUserIdAndProductIdAndSize(user.getId(), cartRequest.getProductId(), cartRequest.getSize());
        Cart cart = new Cart();
        double price = 0;
        if (cartItemExist.isPresent()) {
            // nếu cart item đã tồn tại thì cập nhật lại bằng cách tạo ra cart item mới từ cái cũ và xóa đi cái cũ
            int quantity = cartItemExist.get().getQuantity() + 1;

            this.validateQuantity(quantity, 10, size.getQuantity());

            cartMapper.cartRequestToCart(cartRequest, cart);
            cart.setQuantity(quantity);
            price = quantity * cartItemExist.get().getProduct().getDiscountedPrice();
            cart.setUpdateBy(emailUser);

            cartRepository.delete(cartItemExist.get());
        } else {
            // tạo ra cart item mới
            // quantity mặc định ban đầu là = 1 (phía FE tự set value = 1)
            cartMapper.cartRequestToCart(cartRequest, cart);
            price = product.getDiscountedPrice();
        }
        cart.setProduct(product);
        cart.setTotalPrice(price);
        cart.setUser(user);
        cart.setCreatedBy(emailUser);

        cart = cartRepository.save(cart);

        return convertCartToCartItemResponse(cart);
    }

    @Override
    @Transactional
    public CartItemResponse updateCartItem(Long id, CartRequest cartRequest) throws CustomException {
        Cart oldCartItem = this.getById(id);

        String emailUser = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(emailUser);

        // check permission
        if (!user.getId().equals(oldCartItem.getUser().getId())) {
            throw new CustomException(
                    "You do not have permission to update this cart item !!!",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }

        if (!Objects.equals(cartRequest.getProductId(), oldCartItem.getProduct().getId())) {
            throw new CustomException(
                    "You can only edit size and quantity information !!!",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }
        Product product = productService.getById(oldCartItem.getProduct().getId());

        // kiểm tra xem size request có tồn tại trong product và có còn hàng hay không
        Size size = this.validateSizeInProduct(product, cartRequest.getSize());

        // kiểm tra xem trong giỏ hàng của user đó có tồn tại cart item có cùng size và cùng product đó hay k
        // nếu có thì gộp làm 1 (tạo ra cái mới), ngược lại thì cập nhật bth
        Optional<Cart> checkCartItemDuplicate = cartRepository.checkCartItemDuplicate(user.getId(), id, product.getId(), cartRequest.getSize());

        Cart cart = new Cart();
        if (checkCartItemDuplicate.isPresent()) {
            // nếu tồn tại hai cart item có cùng product id và size thì sẽ tạo ra cart item mới từ hai cái đó
            int quantity = cartRequest.getQuantity() + checkCartItemDuplicate.get().getQuantity();
            // validate quantity (nhỏ hơn 10 và nhỏ hơn product trong stock)
            quantity = Math.min(quantity, size.getQuantity());
            quantity = Math.min(quantity, 10);

            cart.setUser(user);
            cart.setProduct(checkCartItemDuplicate.get().getProduct());
            cart.setSize(checkCartItemDuplicate.get().getSize());
            cart.setQuantity(quantity);
            double price = checkCartItemDuplicate.get().getProduct().getDiscountedPrice() * quantity;
            cart.setTotalPrice(price);
            cart.setCreatedBy(emailUser);
            cart.setUpdateBy(emailUser);

            // xóa đi hai cart có product id và size trùng nhau và gom lại thành 1
            cartRepository.delete(oldCartItem);
            cartRepository.delete(checkCartItemDuplicate.get());

            return convertCartToCartItemResponse(cartRepository.save(cart));
        } else {
            // check quantity request
            this.validateQuantity(cartRequest.getQuantity(), 10, size.getQuantity());
            cartMapper.cartRequestToCart(cartRequest, oldCartItem);
            oldCartItem.setTotalPrice(oldCartItem.getProduct().getDiscountedPrice() * cartRequest.getQuantity());
            oldCartItem.setUpdateBy(user.getEmail());

            return convertCartToCartItemResponse(cartRepository.save(oldCartItem));
        }
    }

    @Override
    @Transactional
    public Response deleteCartItem(Long id) throws CustomException {
        Cart cart = this.getById(id);

        String emailUser = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(emailUser);

        if (!user.getId().equals(cart.getUser().getId())) {
            throw new CustomException(
                    "You do not have permission to delete this cart item !!!",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }
        cartRepository.delete(cart);
        Response response = new Response();
        response.setMessage("Delete cart item success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Transactional
    @Override
    public Response deleteMultiCartItem(List<Long> idCarts) throws CustomException {
        String emailUser = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(emailUser);

        // lấy ra danh sách cart theo list id request
        List<Cart> carts = cartRepository.findAllById(idCarts);

        List<Cart> cartsDelete = carts.stream().filter(c -> c.getUser().getId().equals(user.getId())).collect(Collectors.toList());

        List<Long> idCartsDelete = cartsDelete.stream().map(Cart::getId).collect(Collectors.toList());

        // lấy ra những id không tìm thấy cart hoặc cart đó user này không có quyền để xóa
        List<Long> idCartsMiss = idCarts.stream().filter(id -> !idCartsDelete.contains(id)).collect(Collectors.toList());

        cartRepository.deleteAll(cartsDelete);

        String message = idCartsMiss.isEmpty() ? "Delete some cart items success !!!" :
                "Delete some cart item success, but some cart item have ids not delete: " + idCartsMiss + " !!!";
        Response response = new Response();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(message);

        return response;
    }

    @Override
    public CartResponse getCartDetails() throws CustomException {
        String emailUser = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(emailUser);

        List<Cart> carts = cartRepository.findByUserIdOrderByIdDesc(user.getId());

        // check out off stock product in cart item
        for (Cart cart : carts) {
            boolean isSizeExist = false;
            Product product = cart.getProduct();
            for (Size s : product.getSizes()) {
                if (s.getName() == cart.getSize()) {
                    isSizeExist = true;
                    if (s.getQuantity() == 0) {
                        cart.setOutOfStock(true);
                    } else if (s.getQuantity() < cart.getQuantity()) {
                        cart.setQuantity(s.getQuantity());
                        cart.setOutOfStock(false);
                    } else {
                        cart.setOutOfStock(false);
                    }
                    break;
                }
            }

            if (isSizeExist) {
                cartRepository.save(cart);
            }else{
                // if size not exist in product
                carts.remove(cart);
                cartRepository.delete(cart);
            }
        }

        List<CartItemResponse> cartItemResponses = carts.stream().map(this::convertCartToCartItemResponse).collect(Collectors.toList());

        CartResponse cartResponse = new CartResponse();
        cartResponse.setListCartItems(cartItemResponses);
        cartResponse.setTotalItems((long) carts.size());

        return cartResponse;
    }

    @Override
    public int countCartItem() throws CustomException {
        String emailUser = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(emailUser);

        return cartRepository.countByUserId(user.getId());
    }
}
