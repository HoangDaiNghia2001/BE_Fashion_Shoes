package com.example.service.implement;

import com.example.Entity.Cart;
import com.example.Entity.Product;
import com.example.Entity.Size;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.exception.CustomException;
import com.example.repository.CartRepository;
import com.example.repository.ProductRepository;
import com.example.request.CartRequest;
import com.example.response.CartItemResponse;
import com.example.response.CartResponse;
import com.example.service.CartService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public CartItemResponse addToCart(CartRequest cartRequest) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        User user = userService.findUserProfileByJwt(token);

        Cart cart = new Cart();
        CartItemResponse cartItemResponse = new CartItemResponse();

        List<Cart> carts = cartRepository.findByUserId(user.getId());

        // Kiểm ra số lượng sản phẩm đã có trong giỏ hàng
        if (carts.size() <= 50) {
            Optional<Product> product = productRepository.findById(cartRequest.getProductId());

            // Kiểm tra xem sản phẩm có tồn tại hay không
            if (product.isPresent()) {
                List<Size> checkSize = product.get().getSizes().stream().filter(s -> Objects.equals(s.getName(), cartRequest.getSize())).toList();

                // Kiểm tra xem size request có tồn tại trong sản phẩm không
                if (checkSize.size() != 0) {
                    List<Cart> checkCartItemExist = cartRepository.findByUserIdAndProductIdAndSize(user.getId(), cartRequest.getProductId(), cartRequest.getSize());

                    // kiểm tra xem sản phẩm có size này đã có trong giỏ hàng hay chưa
                    if (checkCartItemExist.size() != 0) {
                        // nếu sản phẩm đã tồn tại thì cập nhật lại bằng cách tạo ra cart item mới từ cái cũ và xóa đi cái cũ
                        int quantity = checkCartItemExist.get(0).getQuantity() + 1;

                        // kiem tra so luong co phu hop voi so luong san pham trong kho hay khong
                        if (Objects.equals(checkSize.get(0).getName(), cartRequest.getSize()) && (checkSize.get(0).getQuantity() < quantity)) {
                            throw new CustomException("You can only buy up to " + checkSize.get(0).getQuantity() + " products for size " + checkSize.get(0).getName());
                        }
                        cart.setUser(user);
                        cart.setQuantity(Math.min(quantity, 10));
                        cart.setTotalPrice(cart.getQuantity() * checkCartItemExist.get(0).getProduct().getDiscountedPrice());
                        cart.setCreatedBy(user.getEmail());
                        cart.setSize(checkCartItemExist.get(0).getSize());
                        cart.setProduct(checkCartItemExist.get(0).getProduct());
                        cart.setUpdateBy(user.getEmail());

                        cartRepository.delete(checkCartItemExist.get(0));
                    } else {
                        cart.setUser(user);
                        cart.setProduct(product.get());
                        cart.setSize(cartRequest.getSize());
                        cart.setQuantity(cartRequest.getQuantity()); // quantity mặc định ban đầu là = 1
                        cart.setTotalPrice(product.get().getDiscountedPrice() * cartRequest.getQuantity());
                        cart.setCreatedBy(user.getEmail());
                    }
                    cart = cartRepository.save(cart);

                    cartItemResponse.setId(cart.getProduct().getId());
                    cartItemResponse.setIdProduct(cart.getProduct().getId());
                    cartItemResponse.setNameProduct(cart.getProduct().getName());
                    cartItemResponse.setTitleProduct(cart.getProduct().getTitle());
                    cartItemResponse.setColor(cart.getProduct().getColor());
                    cartItemResponse.setTotalPrice(cart.getTotalPrice());
                    cartItemResponse.setSize(cart.getSize());
                    cartItemResponse.setMainImageBase64(cart.getProduct().getMainImageBase64());
                    cartItemResponse.setQuantity(cart.getQuantity());
                    cartItemResponse.setSizeProduct(cart.getProduct().getSizes());
                    return cartItemResponse;
                } else {
                    throw new CustomException("Product not have size:" + cartRequest.getSize() + " !!!");
                }
            } else {
                throw new CustomException("Product not found with id: " + cartRequest.getProductId());
            }
        } else {
            throw new CustomException("Your shopping cart is full, please delete products !!!");
        }
    }

    @Override
    @Transactional
    public CartItemResponse updateCartItem(CartRequest cartRequest, Long id) throws CustomException {
        Optional<Cart> oldCartItem = cartRepository.findById(id);

        Cart cart = new Cart();

        if (oldCartItem.isPresent()) {
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
            User user = userService.findUserProfileByJwt(token);

            if (user.getId().equals(oldCartItem.get().getUser().getId())) {
                Optional<Product> product = productRepository.findById(cartRequest.getProductId());
                if (product.isEmpty()) {
                    throw new CustomException("Product not found !!!");
                }
                for (Size size : product.get().getSizes()) {
                    if (Objects.equals(size.getName(), cartRequest.getSize()) && (size.getQuantity() < cartRequest.getQuantity())) {
                        throw new CustomException("You can only buy up to " + size.getQuantity() + " products for size " + size.getName());
                    }
                }

                List<Cart> cartOfUser = cartRepository.findByUserIdOrderByIdDesc(user.getId());

                // kiểm tra xem trong giỏ hàng của user đó có tồn tại sản phẩm có cùng size hay k
                // nếu có thì gộp làm 1 ngược lại thì cập nhật bth
                List<Cart> checkCartExist = cartOfUser.stream().filter(c ->
                        (c.getSize() == cartRequest.getSize()
                                && Objects.equals(c.getProduct().getId(), cartRequest.getProductId())
                                && !Objects.equals(c.getId(), id))).toList();

                if (checkCartExist.size() > 0) {
                    int quantity = cartRequest.getQuantity() + checkCartExist.get(0).getQuantity();
                    cart.setUser(user);
                    cart.setProduct(checkCartExist.get(0).getProduct());
                    cart.setSize(checkCartExist.get(0).getSize());
                    cart.setQuantity(Math.min(quantity, 10));
                    cart.setTotalPrice(checkCartExist.get(0).getProduct().getDiscountedPrice() * cart.getQuantity());
                    cart.setCreatedBy(user.getEmail());
                    cart.setUpdateBy(user.getEmail());

                    cartRepository.delete(oldCartItem.get());
                    cartRepository.delete(checkCartExist.get(0));

                    cart = cartRepository.save(cart);
                } else {
                    oldCartItem.get().setSize(cartRequest.getSize());
                    oldCartItem.get().setQuantity(cartRequest.getQuantity());
                    oldCartItem.get().setTotalPrice(oldCartItem.get().getProduct().getDiscountedPrice() * cartRequest.getQuantity());
                    oldCartItem.get().setUpdateBy(user.getEmail());

                    cart = cartRepository.save(oldCartItem.get());
                }

                CartItemResponse cartItemResponse = new CartItemResponse();

                cartItemResponse.setId(cart.getId());
                cartItemResponse.setIdProduct(cart.getProduct().getId());
                cartItemResponse.setNameProduct(cart.getProduct().getName());
                cartItemResponse.setTitleProduct(cart.getProduct().getTitle());
                cartItemResponse.setColor(cart.getProduct().getColor());
                cartItemResponse.setTotalPrice(cart.getTotalPrice());
                cartItemResponse.setSize(cart.getSize());
                cartItemResponse.setMainImageBase64(cart.getProduct().getMainImageBase64());
                cartItemResponse.setQuantity(cart.getQuantity());
                cartItemResponse.setSizeProduct(cart.getProduct().getSizes());

                return cartItemResponse;
            } else {
                throw new CustomException("You do not have permission to update !!!");
            }
        } else {
            throw new CustomException("Cart item not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public String deleteCartItem(Long id) throws CustomException {
        Optional<Cart> cart = cartRepository.findById(id);

        if (cart.isPresent()) {
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
            User user = userService.findUserProfileByJwt(token);

            if (user.getId().equals(cart.get().getUser().getId())) {
                cartRepository.delete(cart.get());
                return "Delete cart item success !!!";
            } else {
                throw new CustomException("You do not have permission to delete !!!");
            }
        } else {
            throw new CustomException("Cart item not found with id: " + id);
        }
    }

    @Transactional
    @Override
    public String deleteMultiCartItem(List<Long> idProducts) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        User user = userService.findUserProfileByJwt(token);

        idProducts.forEach(id -> {
            Optional<Cart> cart = cartRepository.findById(id);
            if (cart.isPresent()) {
                if (cart.get().getUser().getId().equals(user.getId())) {
                    cartRepository.delete(cart.get());
                } else {
                    try {
                        throw new CustomException("You not permission to delete this cart item !!!");
                    } catch (CustomException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                try {
                    throw new CustomException("Cart item not found with id: " + id);
                } catch (CustomException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return "Delete some cart item success !!!";
    }

    @Override
    public CartResponse getCartDetails() throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        User user = userService.findUserProfileByJwt(token);

        List<Cart> carts = cartRepository.findByUserIdOrderByIdDesc(user.getId());

        List<CartItemResponse> cartItemResponses = new ArrayList<>();

        carts.forEach(cart -> {
            CartItemResponse cartItemResponse = new CartItemResponse();

            cartItemResponse.setId(cart.getId());
            cartItemResponse.setIdProduct(cart.getProduct().getId());
            cartItemResponse.setNameProduct(cart.getProduct().getName());
            cartItemResponse.setTitleProduct(cart.getProduct().getTitle());
            cartItemResponse.setColor(cart.getProduct().getColor());
            cartItemResponse.setTotalPrice(cart.getTotalPrice());
            cartItemResponse.setSize(cart.getSize());
            cartItemResponse.setMainImageBase64(cart.getProduct().getMainImageBase64());
            cartItemResponse.setQuantity(cart.getQuantity());
            cartItemResponse.setSizeProduct(cart.getProduct().getSizes());

            cartItemResponses.add(cartItemResponse);
        });
        CartResponse cartResponse = new CartResponse();
        cartResponse.setListCartItems(cartItemResponses);
        cartResponse.setTotalItems((long) carts.size());

        return cartResponse;
    }

    @Override
    public int countCartItem() throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        User user = userService.findUserProfileByJwt(token);

        return cartRepository.countByUserId(user.getId());
    }
}
