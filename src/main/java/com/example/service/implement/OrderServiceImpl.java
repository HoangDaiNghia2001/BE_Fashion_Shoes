package com.example.service.implement;

import com.example.Entity.*;
import com.example.config.JwtProvider;
import com.example.config.VNPayConfig;
import com.example.constant.CookieConstant;
import com.example.constant.OrderConstant;
import com.example.exception.CustomException;
import com.example.repository.*;
import com.example.request.OrderProductQuantityRequest;
import com.example.request.OrderRequest;
import com.example.request.OrderUpdateRequest;
import com.example.response.*;
import com.example.service.OrderService;
import com.example.service.UserService;
import com.example.util.EmailUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private VNPayRepository vnPayRepository;

    @Autowired
    private EmailUtil emailUtil;

    private String generateUniqueCode() {
        String code;
        Order existingOrder;
        do {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
            code = LocalDate.now().toString().replaceAll("-","") + "_" + uuid;
            existingOrder = orderRepository.findByCode(code);
        } while (existingOrder != null);

        return code.toUpperCase();
    }

    @Override
    @Transactional
    public void placeOrderCOD(OrderRequest orderRequest) throws CustomException, MessagingException {
        // nếu số lượng sản phẩm order nhiều hơn số lượng sản phẩm có trong kho thì
        // nhân viên phải liên hệ khách hàng để thỏa thuận lại về đơn hàng

        // do mua hàng thông qua cart nên phải check xem các sản phẩm request có đang nằm trong giỏ hàng của user không !!!
        // sau khi đặt hàng thành công thì sẽ xóa các sản phẩm đã đặt ở trong giỏ hàng của user

        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        User user = userService.findUserProfileByJwt(token);

        List<OrderProductQuantityRequest> orderProductQuantityRequests = orderRequest.getProductQuantities();

        List<Cart> cartOfUser = cartRepository.findByUserIdOrderByIdDesc(user.getId());

        List<Boolean> checkExists = new ArrayList<>();

        // kiem tra xem san pham request co dang nam trong gio hang hay khong
        orderProductQuantityRequests.forEach(p -> {
            checkExists.add(cartOfUser.stream().anyMatch(c -> (Objects.equals(c.getProduct().getId(), p.getProductId()) && (c.getSize() == p.getSize()))));
        });

        boolean existItem = checkExists.stream().anyMatch(exist -> !exist);

        if (existItem) {
            throw new CustomException("Some products not exits in your cart, Please check again and follow the correct steps !!!");
        }

        double totalPrice = 0;
        // create order
        Order order = new Order();

        order.setCode(generateUniqueCode());
        order.setAddress(orderRequest.getAddress());
        order.setDistrict(orderRequest.getDistrict());
        order.setProvince(orderRequest.getProvince());
        order.setWard(orderRequest.getWard());
        order.setFullName(orderRequest.getFullName());
        order.setUser(user);
        order.setAlternatePhoneNumber(orderRequest.getAlternatePhoneNumber());
        order.setPhoneNumber(orderRequest.getPhoneNumber());
        order.setStatus(OrderConstant.ORDER_PENDING);
        order.setTransportFee(orderRequest.getTransportFee());
        order.setCreatedBy(user.getEmail());
        for (OrderProductQuantityRequest p : orderProductQuantityRequests) {
            totalPrice += p.getTotalPrice();
        }
        order.setTotalPrice(totalPrice + order.getTransportFee());
        order.setNote(orderRequest.getNote());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setOrderDate(LocalDateTime.now());
        order.setPay(OrderConstant.ORDER_UNPAID);
        order = orderRepository.save(order); // => order success

        // create order line
        for (OrderProductQuantityRequest productOrder : orderProductQuantityRequests) {
            Optional<Product> product = productRepository.findById(productOrder.getProductId());

            if (!product.isPresent()) {
                throw new CustomException("Product not found !!!");
            }
            OrderLine orderLine = new OrderLine();

            orderLine.setOrder(order);
            orderLine.setProduct(product.get());
            orderLine.setQuantity(productOrder.getQuantity());
            orderLine.setSize(productOrder.getSize());
            orderLine.setCreatedBy(user.getEmail());
            orderLine.setTotalPrice(productOrder.getTotalPrice());

            orderLineRepository.save(orderLine);

            // delete product in cart of user
            cartOfUser.forEach(c -> {
                boolean check = orderProductQuantityRequests.stream().anyMatch(p -> (p.getSize() == c.getSize() && Objects.equals(p.getProductId(), c.getProduct().getId())));
                if (check) {
                    cartRepository.delete(c);
                }
            });

            OrderResponse orderResponse = getOrderNewestByEmail();
            emailUtil.sendOrderEmail(orderResponse);
        }
    }

    @Override
    public String placeOrderVnPay(long totalPrice, String orderInfo, String orderId) {

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(totalPrice * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo + "-" + orderId);
        vnp_Params.put("vnp_OrderType", VNPayConfig.orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", VNPayConfig.vnp_IpAddr);

        // chạy localhost
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // chạy trên railway giờ London
//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String vnp_CreateDate = formatter.format(cld.getTime());
//        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // do lệch múi giờ ở phần domain là 7 tiếng (giờ ở Lodon với giờ ở Việt Nam) nên phải set up vnp_ExpireDate có thời gian là 8 tiếng
        cld.add(Calendar.MINUTE, 480);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    private OrderResponse generateOrderResponse(Order order){
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setId(order.getId());
        orderResponse.setCode(order.getCode());
        orderResponse.setFullName(order.getFullName());
        orderResponse.setEmail(order.getCreatedBy());
        orderResponse.setPay(order.getPay());
        orderResponse.setPhoneNumber(order.getPhoneNumber());
        orderResponse.setAlternatePhone(order.getAlternatePhoneNumber());
        orderResponse.setAddress(order.getAddress());
        orderResponse.setWard(order.getWard());
        orderResponse.setDistrict(order.getDistrict());
        orderResponse.setProvince(order.getProvince());
        orderResponse.setNotes(order.getNote());
        orderResponse.setDeliveryDate(order.getDeliveryDate());
        orderResponse.setReceivingDate(order.getReceivingDate());
        orderResponse.setUpdateAtUser(order.getUpdateAtUser());
        orderResponse.setUpdateByUser(order.getUpdateByUser());
        orderResponse.setPaymentMethod(order.getPaymentMethod());
        orderResponse.setStatusOrder(order.getStatus());
        orderResponse.setTotalPrice(order.getTotalPrice());
        orderResponse.setTransportFee(order.getTransportFee());
        orderResponse.setOrderDate(order.getOrderDate());
        List<OrderLineResponse> orderLineResponseList = new ArrayList<>();

        List<OrderLine> orderLines = orderLineRepository.findByOrderId(order.getId());

        orderLines.forEach(orderLine -> {
            Optional<Product> product = productRepository.findById(orderLine.getProduct().getId());

            OrderLineResponse orderLineResponse = new OrderLineResponse();

            orderLineResponse.setProductId(product.get().getId());
            orderLineResponse.setCodeProduct(product.get().getCode());
            orderLineResponse.setBrand(product.get().getBrandProduct().getName());
            orderLineResponse.setMainImageBase64(product.get().getMainImageBase64());
            orderLineResponse.setQuantity(orderLine.getQuantity());
            orderLineResponse.setSize(orderLine.getSize());
            orderLineResponse.setNameProduct(product.get().getName());
            orderLineResponse.setTotalPrice(orderLine.getTotalPrice());

            orderLineResponseList.add(orderLineResponse);
        });

        orderResponse.setOrderLines(orderLineResponseList);

        return orderResponse;
    }

    @Override
    public List<OrderResponse> getOrdersResponse(List<Order> orders) {
        List<OrderResponse> orderResponseList = new ArrayList<>();

        orders.forEach(order -> {
            OrderResponse orderResponse = generateOrderResponse(order);
            orderResponseList.add(orderResponse);
        });
        return orderResponseList;
    }

    @Override
    public List<OrderResponse> getOrderDetailsByUser(String orderStatus, String paymentMethod,
                                                     LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                                     LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                                     LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        User user = userService.findUserProfileByJwt(token);

        List<Order> ordersOfUser = orderRepository.getOrdersByUser(user.getId(), orderStatus, paymentMethod,
                orderDateStart, orderDateEnd,
                deliveryDateStart, deliveryDateEnd,
                receivingDateStart, receivingDateEnd);

        return getOrdersResponse(ordersOfUser);
    }

    @Override
    public ListOrdersResponse getAllOrderDetailByAdmin(String orderBy, String phoneNumber, String orderStatus, String paymentMethod,
                                                       String province, String district, String ward,
                                                       LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                                       LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                                       LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd,
                                                       int pageIndex, int pageSize) {

        List<Order> orderList = orderRepository.getOrdersByAdmin(orderBy, phoneNumber, orderStatus, paymentMethod, province, district,
                ward, orderDateStart, orderDateEnd, deliveryDateStart, deliveryDateEnd, receivingDateStart, receivingDateEnd);

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), orderList.size());

        ListOrdersResponse listOrdersResponse = new ListOrdersResponse();
        listOrdersResponse.setListOrders(getOrdersResponse(orderList.subList(startIndex, endIndex)));
        listOrdersResponse.setTotal((long) orderList.size());

        return listOrdersResponse;
    }

    @Override
    public OrderResponse getOrderDetail(Long orderId) throws CustomException {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
            User user = userService.findUserProfileByJwt(token);
            if (user.getId().equals(order.get().getUser().getId())) {

                OrderResponse orderResponse = new OrderResponse();

                orderResponse.setId(order.get().getId());
                orderResponse.setFullName(order.get().getFullName());
                orderResponse.setPhoneNumber(order.get().getPhoneNumber());
                orderResponse.setAlternatePhone(order.get().getAlternatePhoneNumber());
                orderResponse.setAddress(order.get().getAddress());
                orderResponse.setWard(order.get().getWard());
                orderResponse.setDistrict(order.get().getDistrict());
                orderResponse.setProvince(order.get().getProvince());
                orderResponse.setNotes(order.get().getNote());

                return orderResponse;

            } else {
                throw new CustomException("You do not have permission to see information of this order !!!");
            }
        } else {
            throw new CustomException("Order not found !!!");
        }
    }

    @Override
    @Transactional
    public void cancelOrderByUser(Long idOrder) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        User user = userService.findUserProfileByJwt(token);

        Optional<Order> order = orderRepository.findById(idOrder);

        if (order.isPresent()) {
            if (order.get().getStatus().equals(OrderConstant.ORDER_PENDING)) {
                // Kiểm tra xem user có sở hữu đơn hàng này không
                if (Objects.equals(user.getId(), order.get().getUser().getId())) {
                    if (order.get().getPaymentMethod().equals("VNPAY")) {
                        VNPayInformation vnPayInformation = vnPayRepository.findByOrderId(order.get().getId());
                        if (vnPayInformation != null) {
                            vnPayRepository.delete(vnPayInformation);
                        }
                    }
                    orderRepository.delete(order.get());
                } else {
                    throw new CustomException("You do not have permission to cancel this order !!!");
                }
            } else {
                throw new CustomException("This order is on its way to you !!!");
            }

        } else {
            throw new CustomException("Not found this order !!!");
        }
    }

    @Override
    @Transactional
    public void markOrderConfirmed(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
            User admin = userService.findUserProfileByJwt(token);

            // cập nhật lại số lượng còn trong kho
            order.get().getOrderLines().forEach(orderLine -> {
                Optional<Product> product = productRepository.findById(orderLine.getProduct().getId());

                if (product.isPresent()) {

                    Set<Size> sizes = new HashSet<>();
                    int quantity = 0;

                    boolean checkSizeExist = false;  // => kiểm tra xem size này có tồn tại không

                    // cập nhật lại số lượng tùng size
                    for (Size size : product.get().getSizes()) {
                        if (size.getName() == orderLine.getSize()) {
                            size.setQuantity(size.getQuantity() - orderLine.getQuantity());
                            sizes.add(size);
                            checkSizeExist = true;
                        }
                        sizes.add(size);
                    }

                    if (!checkSizeExist) {
                        try {
                            throw new CustomException("Product " + product.get().getId() + " not have size " + orderLine.getSize() + " !!!");
                        } catch (CustomException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // cập nhật lại tổng số lượng của sản phẩm
                    for (Size size : sizes) {
                        quantity += size.getQuantity();
                    }

                    product.get().setQuantity(quantity);
                    product.get().setSizes(sizes);

                    productRepository.save(product.get());
                } else {
                    try {
                        throw new CustomException("Product not found with id: " + orderLine.getProduct().getId());
                    } catch (CustomException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            order.get().setStatus(OrderConstant.ORDER_CONFIRMED);
            order.get().setUpdateBy(admin.getEmail());

            orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found with id " + id);
        }
    }

    @Override
    @Transactional
    public void markOrderShipped(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
            User admin = userService.findUserProfileByJwt(token);

            order.get().setStatus(OrderConstant.ORDER_SHIPPED);
            order.get().setDeliveryDate(LocalDateTime.now());
            order.get().setUpdateBy(admin.getEmail());

            orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found with id " + id);
        }
    }

    @Override
    @Transactional
    public void markOrderDelivered(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
            User admin = userService.findUserProfileByJwt(token);

            order.get().setPay(OrderConstant.ORDER_PAID);
            order.get().setStatus(OrderConstant.ORDER_DELIVERED);
            order.get().setReceivingDate(LocalDateTime.now());
            order.get().setUpdateBy(admin.getEmail());

            orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found with id " + id);
        }
    }

    @Override
    @Transactional
    public void deleteOrderByAdmin(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isEmpty()) {

            throw new CustomException("Not found order have id: " + id + " !!!");
        }
        orderRepository.delete(order.get());
    }

    @Override
    @Transactional
    public void deleteSomeOrdersByAdmin(List<Long> listIdOrder) throws CustomException {
        for (Long id : listIdOrder) {
            deleteOrderByAdmin(id);
        }
    }

    @Override
    public long findOrderIdNewest() {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        String email = (String) jwtProvider.getClaimsFormToken(token).get("email");

        Order order = orderRepository.getOrderIdNewest(email);
        return order.getId();
    }

    @Override
    public OrderResponse getOrderNewestByEmail() {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
        String email = (String) jwtProvider.getClaimsFormToken(token).get("email");

        Order order = orderRepository.getOrderIdNewest(email);

        return generateOrderResponse(order);
    }

    @Transactional
    @Override
    public void updatePayOfOrderVNPay(String vnp_ResponseCode, Long orderId) throws CustomException {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            if (vnp_ResponseCode.equals("00")) {
                order.get().setPay(OrderConstant.ORDER_PAID);
            } else {
                order.get().setPay(OrderConstant.ORDER_UNPAID);
            }
            orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found !!!");
        }
    }

    @Transactional
    @Override
    public void updatePayOfOrderPayPal(String approved, Long orderId) throws CustomException {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            if (approved.equals("approved")) {
                order.get().setPay(OrderConstant.ORDER_PAID);
            } else {
                order.get().setPay(OrderConstant.ORDER_UNPAID);
            }
            orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found !!!");
        }
    }

    @Transactional
    @Override
    public void updateOrderByUser(Long orderId, OrderUpdateRequest orderUpdateRequest) throws CustomException {
        Optional<Order> orderUpdate = orderRepository.findById(orderId);
        if (orderUpdate.isPresent()) {
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);
            User user = userService.findUserProfileByJwt(token);
            if (orderUpdate.get().getUser().getId().equals(user.getId())) {
                orderUpdate.get().setFullName(orderUpdateRequest.getFullName());
                orderUpdate.get().setPhoneNumber(orderUpdateRequest.getPhoneNumber());
                orderUpdate.get().setAlternatePhoneNumber(orderUpdateRequest.getAlternatePhone());
                orderUpdate.get().setAddress(orderUpdateRequest.getAddress());
                orderUpdate.get().setWard(orderUpdateRequest.getWard());
                orderUpdate.get().setDistrict(orderUpdateRequest.getDistrict());
                orderUpdate.get().setProvince(orderUpdateRequest.getProvince());
                orderUpdate.get().setNote(orderUpdateRequest.getNotes());
                orderUpdate.get().setUpdateAtUser(LocalDateTime.now());
                orderUpdate.get().setUpdateByUser(user.getEmail());

                orderRepository.save(orderUpdate.get());
            } else {
                throw new CustomException("You do not have permission to update this order !!!");
            }
        } else {
            throw new CustomException("Order not found !!!");
        }
    }

    @Override
    public Long totalOrders() {
        return orderRepository.count();
    }

    @Override
    public Double revenue() {
        return orderRepository.sumTotalPrice();
    }

    @Override
    public List<QuantityByBrandResponse> quantityProductSoldByBrand() {
        return orderLineRepository.quantityProductSoldByBrand();
    }

    @Override
    public List<OrderStatisticalByYearResponse> statisticByYear(int year) {
        List<OrderStatisticalByYearResponse> statistical = orderRepository.statisticByYear(year);

        for (int month = 1; month <= 12; month++) {
            final int currentMonth = month;
            Optional<OrderStatisticalByYearResponse> monthExist = statistical.stream()
                    .filter(item -> Objects.equals(item.getMonth(), currentMonth))
                    .findFirst();

            if (!monthExist.isPresent()) {
                statistical.add(new OrderStatisticalByYearResponse(currentMonth, 0));
            }
        }

        return statistical.stream()
                .sorted((a, b) -> Integer.compare(a.getMonth(), b.getMonth()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllYearInOrder() {
        return orderRepository.getAllYearInOrder();
    }

    @Override
    public long averageOrdersValue() {
        return Math.round(orderRepository.averageOrdersValue());
    }

    @Override
    public long sold() {
        return orderLineRepository.sold();
    }
}
