package com.example.service.implement;

import com.example.Entity.*;
import com.example.config.VNPayConfig;
import com.example.constant.OrderConstant;
import com.example.exception.CustomException;
import com.example.mapper.OrderLineMapper;
import com.example.mapper.OrderMapper;
import com.example.repository.*;
import com.example.request.OrderLineRequest;
import com.example.request.OrderRequest;
import com.example.response.*;
import com.example.service.EmailService;
import com.example.service.OrderService;
import com.example.service.ProductService;
import com.example.service.UserService;
import com.example.util.MethodUtils;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private VNPayRepository vnPayRepository;
    @Autowired
    private MethodUtils methodUtils;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderLineMapper orderLineMapper;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private String generateUniqueCode() {
        String code;
        Order existingOrder;
        do {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
            code = LocalDate.now().toString().replaceAll("-", "") + "_" + uuid;
            existingOrder = orderRepository.findByCode(code);
        } while (existingOrder != null);

        return code.toUpperCase();
    }

    // kiểm tra xem product id, size có tồn tại trong giỏ hàng của user trong db hay không và số lượng request có phù hợp với số lượng hàng trong kho k
    private boolean checkValidOrderLineRequest(List<OrderLineRequest> orderLineRequests,
                                               List<Cart> cartOfUser) throws CustomException {
        List<Boolean> matchingCartItems = new ArrayList<>();
        orderLineRequests.forEach(o -> {
            matchingCartItems.add(cartOfUser.stream().anyMatch(c -> (c.getProduct().getId().equals(o.getProductId()) &&
                    c.getSize() == o.getSize())));
        });

        // kiểm tra xem product id và size name của mỗi OrderLineRequest có tồn tại trong giỏ hàng của User hay không
        boolean checkNotMatching = matchingCartItems.stream().anyMatch(value -> !value);
        if (checkNotMatching) {
            throw new CustomException(
                    "Some order line items are not in the user's shopping cart !!!",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        // kiểm tra số lượng request của OrderLineItem có phù hợp với trong kho hay không
        for (OrderLineRequest o : orderLineRequests) {
            Product product = productService.getById(o.getProductId());
            for (Size s : product.getSizes()) {
                if (s.getName() == o.getSize() && s.getQuantity() < o.getQuantity()) {
                    throw new CustomException(
                            "Product: " + product.getName() + " with size: " + o.getSize() + " insufficient quantity required !!!",
                            HttpStatus.BAD_REQUEST.value()
                    );
                }
            }
        }
        return true;
    }

    private Order createOrder(OrderRequest orderRequest, User user) {
        double price = orderRequest.getOrderLineRequests().stream()
                .mapToDouble(OrderLineRequest::getTotalPrice)
                .sum();

        Order order = new Order();
        orderMapper.orderRequestToOrder(orderRequest, order);
        order.setCode(this.generateUniqueCode());
        order.setUser(user);
        order.setStatus(OrderConstant.ORDER_PENDING);
        order.setCreatedBy(user.getEmail());
        order.setTotalPrice(price + orderRequest.getTransportFee());
        order.setOrderDate(LocalDateTime.now());
        order.setPay(OrderConstant.ORDER_UNPAID);

        return orderRepository.save(order);
    }

    private void createOrderLine(List<OrderLineRequest> orderLineRequests, Order order, User user) throws CustomException {
        for (OrderLineRequest orderLineRequest : orderLineRequests) {
            Product product = productService.getById(orderLineRequest.getProductId());

            OrderLine orderLine = new OrderLine();
            orderLineMapper.orderLineRequestToOrderLine(orderLineRequest, orderLine);
            orderLine.setOrder(order);
            orderLine.setProduct(product);
            orderLine.setCreatedBy(user.getEmail());

            orderLineRepository.save(orderLine);
        }
    }

    // xóa các cart item tương ứng trong db khi order thành công
    private void deleteCartItemByOrder(List<Cart> cartOfUser,
                                       List<OrderLineRequest> orderLineRequests) {
        cartOfUser.forEach(c -> {
            boolean check = orderLineRequests.stream().anyMatch(o -> (o.getSize() == c.getSize() &&
                    o.getProductId().equals(c.getProduct().getId())));
            if (check) {
                cartRepository.delete(c);
            }
        });
    }

    private OrderResponse generateOrderResponse(Order order) throws CustomException {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        OrderResponse orderResponse = orderMapper.orderToOrderResponse(order);
        orderResponse.setEmail(order.getCreatedBy());

        List<OrderLineResponse> orderLineResponseList = new ArrayList<>();

        List<OrderLine> orderLines = orderLineRepository.findByOrderId(order.getId());

        for (OrderLine orderLine : orderLines) {
            Product product = productService.getById(orderLine.getProduct().getId());

            OrderLineResponse orderLineResponse = new OrderLineResponse();

            orderLineResponse.setProductId(product.getId());
            orderLineResponse.setCodeProduct(product.getCode());
            orderLineResponse.setBrand(product.getBrandProduct().getName());
            orderLineResponse.setMainImageBase64(product.getMainImageBase64());
            orderLineResponse.setQuantity(orderLine.getQuantity());
            orderLineResponse.setSize(orderLine.getSize());
            orderLineResponse.setNameProduct(product.getName());
            orderLineResponse.setTotalPrice(decimalFormat.format(Math.round(orderLine.getTotalPrice())));

            orderLineResponseList.add(orderLineResponse);
        }

        orderResponse.setOrderLines(orderLineResponseList);

        return orderResponse;
    }

    private List<OrderResponse> generateOrderListToOrderResponseList(List<Order> orders) throws CustomException {
        List<OrderResponse> orderResponseList = new ArrayList<>();

        for (Order order : orders) {
            OrderResponse orderResponse = this.generateOrderResponse(order);
            orderResponseList.add(orderResponse);
        }
        return orderResponseList;
    }

    private Context prepareOrderContext(Order order) throws CustomException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        OrderResponse orderResponse = this.generateOrderResponse(order);
        Context context = new Context();
        context.setVariable("orderCode", orderResponse.getCode());
        context.setVariable("orderStatus", orderResponse.getStatus());
        context.setVariable("fullName", orderResponse.getFullName());
        context.setVariable("orderLines", orderResponse.getOrderLines());
        context.setVariable("feeShipping", orderResponse.getTransportFee() != 0 ? decimalFormat.format(Math.round( orderResponse.getTransportFee())) : "Free");
        context.setVariable("totalAmount", decimalFormat.format(Math.round(orderResponse.getTotalPrice())));
        context.setVariable("pay", orderResponse.getPay());
        context.setVariable("phoneNumber", orderResponse.getPhoneNumber());
        context.setVariable("paymentMethod", orderResponse.getPaymentMethod());
        context.setVariable("orderDate", orderResponse.getOrderDate().format(formatter));

        return context;
    }

    @Override
    public Order getById(Long id) throws CustomException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Order not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        return order;
    }

    @Override
    @Transactional
    public void placeOrderCOD(OrderRequest orderRequest) throws CustomException{
        // kiểm tra só lượng sản phẩm trong order line so với sản phẩm còn trong kho
        // do mua hàng thông qua cart nên phải check xem các sản phẩm request có đang nằm trong giỏ hàng của user không !!!
        // sau khi đặt hàng thành công thì sẽ xóa các sản phẩm đã đặt ở trong giỏ hàng của user

        String emailUser = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(emailUser);

        List<OrderLineRequest> orderLineRequests = orderRequest.getOrderLineRequests();

        List<Cart> cartOfUser = cartRepository.findByUserIdOrderByIdDesc(user.getId());

        boolean checkValid = this.checkValidOrderLineRequest(orderLineRequests, cartOfUser);

        if (checkValid) {
            // create order
            Order order = this.createOrder(orderRequest, user);

            // create order line
            this.createOrderLine(orderLineRequests, order, user);

            // xóa các cart item tương ứng
            this.deleteCartItemByOrder(cartOfUser, orderLineRequests);
        }
    }

    @Override
    @Transactional
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
//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String vnp_CreateDate = formatter.format(cld.getTime());
//        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // chạy trên railway giờ London
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

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

    @Override
    public ListOrdersResponse getOrderDetailsByUser(String orderStatus, String paymentMethod,
                                                    LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                                    LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                                    LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd, String orderCode) throws CustomException {
        String email = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(email);

        List<Order> ordersOfUser = orderRepository.getOrdersByUser(user.getId(), orderStatus, paymentMethod,
                orderDateStart, orderDateEnd,
                deliveryDateStart, deliveryDateEnd,
                receivingDateStart, receivingDateEnd, orderCode);

        ListOrdersResponse listOrdersResponse = new ListOrdersResponse();
        listOrdersResponse.setListOrders(this.generateOrderListToOrderResponseList(ordersOfUser));
        listOrdersResponse.setTotal((long) ordersOfUser.size());

        return listOrdersResponse;
    }

    @Override
    public ListOrdersResponse getAllOrderDetailByAdmin(String orderBy, String phoneNumber, String orderStatus, String paymentMethod,
                                                       String province, String district, String ward,
                                                       LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                                       LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                                       LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd,
                                                       int pageIndex, int pageSize) throws CustomException {

        List<Order> orderList = orderRepository.getOrdersByAdmin(orderBy, phoneNumber, orderStatus, paymentMethod, province, district,
                ward, orderDateStart, orderDateEnd, deliveryDateStart, deliveryDateEnd, receivingDateStart, receivingDateEnd);

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), orderList.size());

        ListOrdersResponse listOrdersResponse = new ListOrdersResponse();
        listOrdersResponse.setListOrders(generateOrderListToOrderResponseList(orderList.subList(startIndex, endIndex)));
        listOrdersResponse.setTotal((long) orderList.size());

        return listOrdersResponse;
    }

    @Override
    public OrderResponse getOrderDetail(Long id) throws CustomException {
        Order order = this.getById(id);

        String email = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(email);
        if (!user.getId().equals(order.getUser().getId())) {
            throw new CustomException(
                    "You do not have permission to see information of this order !!!",
                    HttpStatus.UNAUTHORIZED.value());
        }
        return orderMapper.orderToOrderResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrderByUser(Long id) throws CustomException {
        String email = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(email);

        Order order = this.getById(id);

        if (!order.getStatus().equals(OrderConstant.ORDER_PENDING)) {
            throw new CustomException(
                    "This order is on its way to you !!!",
                    HttpStatus.BAD_REQUEST.value());
        }
        // Kiểm tra xem user có sở hữu đơn hàng này không
        if (!Objects.equals(user.getId(), order.getUser().getId())) {
            throw new CustomException(
                    "You do not have permission to cancel this order !!!",
                    HttpStatus.UNAUTHORIZED.value());
        }
        if (order.getPaymentMethod().equals("VNPAY")) {
            VNPayInformation vnPayInformation = vnPayRepository.findByOrderId(order.getId());
            if (vnPayInformation != null) {
                vnPayRepository.delete(vnPayInformation);
            }
        }
        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public void markOrderConfirmed(Long id) throws CustomException, MessagingException {
        Order order = this.getById(id);

        String email = methodUtils.getEmailFromTokenOfAdmin();

        // cập nhật lại số lượng còn trong kho
        for (OrderLine orderLine : order.getOrderLines()) {
            Product product = productService.getById(orderLine.getProduct().getId());

            boolean checkSizeExist = false;  // kiểm tra xem size này có tồn tại không

            Set<Size> sizes = new HashSet<>();
            // cập nhật lại số lượng cho từng size
            for (Size size : product.getSizes()) {
                if (size.getName() == orderLine.getSize()) {
                    // kiểm tra số lượng trong kho có phù hợp không
                    if(size.getQuantity() == 0){
                        throw new CustomException(
                                "Product code: " + product.getCode() + " with size: " + size.getName() + " is out of stock !!!",
                                HttpStatus.BAD_REQUEST.value()
                        );
                    }
                    if(size.getQuantity() < orderLine.getQuantity()){
                        throw new CustomException(
                                "Product code: " + product.getCode() + " with size: " + size.getName() + " insufficient quantity required !!!",
                                HttpStatus.BAD_REQUEST.value()
                        );
                    }
                    size.setQuantity(size.getQuantity() - orderLine.getQuantity());
                    checkSizeExist = true;
                }
                sizes.add(size);
            }

            if (!checkSizeExist) {
                throw new CustomException(
                        "Product " + product.getId() + " not have size " + orderLine.getSize() + " !!!",
                        HttpStatus.BAD_REQUEST.value());
            }

            // cập nhật danh sách size mới sau khi cập nhật số lương từng size
            product.setSizes(sizes);
            int quantity = product.getSizes().stream().mapToInt(Size::getQuantity).sum();
            product.setQuantity(quantity);

            // update product
            productRepository.save(product);
        }

        order.setStatus(OrderConstant.ORDER_CONFIRMED);
        order.setUpdateBy(email);

        // update status order
        order = orderRepository.save(order);

        // send email to the user
        Context context = this.prepareOrderContext(order);
        emailService.sendEmail(order.getUser().getEmail(), "Success! Your Order Has Been Confirmed", "order_email", context);
    }

    @Override
    @Transactional
    public void markOrderShipped(Long id) throws CustomException, MessagingException {
        Order order = this.getById(id);

        String email = methodUtils.getEmailFromTokenOfAdmin();

        order.setStatus(OrderConstant.ORDER_SHIPPED);
        order.setDeliveryDate(LocalDateTime.now());
        order.setUpdateBy(email);

        order = orderRepository.save(order);

        // send email to the user
        Context context = this.prepareOrderContext(order);
        emailService.sendEmail(order.getUser().getEmail(), "Success! Your Order Has Been Shipped", "order_email", context);
    }

    @Override
    @Transactional
    public void markOrderDelivered(Long id) throws CustomException, MessagingException {
        Order order = this.getById(id);

        String email = methodUtils.getEmailFromTokenOfAdmin();


        order.setPay(OrderConstant.ORDER_PAID);
        order.setStatus(OrderConstant.ORDER_DELIVERED);
        order.setReceivingDate(LocalDateTime.now());
        order.setUpdateBy(email);

        order = orderRepository.save(order);

        // send email to the user
        Context context = this.prepareOrderContext(order);
        emailService.sendEmail(order.getUser().getEmail(), "Success! Your Order Has Been Delivered", "order_email", context);
    }

    @Override
    @Transactional
    public Response deleteOrderByAdmin(Long id) throws CustomException, MessagingException {
        Order order = this.getById(id);
        if(!order.getStatus().equals(OrderConstant.ORDER_DELIVERED)){
            // send email to the user
            Context context = this.prepareOrderContext(order);
            context.setVariable("orderCancelled", true);
            emailService.sendEmail(order.getUser().getEmail(), "OOP! Your Order Has Been Cancelled", "order_email", context);
        }
        orderRepository.delete(order);

        Response response = new Response();
        response.setMessage("Delete order success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    @Transactional
    public Response deleteSomeOrdersByAdmin(List<Long> listIdOrder) throws CustomException, MessagingException {
        List<Order> lstOrderDelete = orderRepository.findAllById(listIdOrder);

        List<Long> lstIdOrderDelete = lstOrderDelete.stream().map(Order::getId).collect(Collectors.toList());

        List<Long> lstIdOrderMiss = listIdOrder.stream().filter(id -> !lstIdOrderDelete.contains(id)).collect(Collectors.toList());

        for(Order order : lstOrderDelete) {
            if (!order.getStatus().equals(OrderConstant.ORDER_DELIVERED)) {
                Context context = this.prepareOrderContext(order);
                context.setVariable("orderCancelled", true);
                emailService.sendEmail(order.getUser().getEmail(), "OOP! Your Order Has Been Cancelled", "order_email", context);
            }
        }

        orderRepository.deleteAll(lstOrderDelete);

        String message = lstIdOrderMiss.isEmpty() ? "Delete some orders success !!!"
                : "Delete some orders success , but have some orders not found: " + lstIdOrderMiss;

        Response response = new Response();
        response.setMessage(message);
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    public long findOrderIdNewest() throws CustomException {
        String email = methodUtils.getEmailFromTokenOfUser();
        Order order = orderRepository.getOrderIdNewest(email);
        return order.getId();
    }

    @Override
    public OrderResponse getOrderNewestByEmail() throws CustomException {
        String email = methodUtils.getEmailFromTokenOfUser();
        Order order = orderRepository.getOrderIdNewest(email);
        return generateOrderResponse(order);
    }

    @Transactional
    @Override
    public void updatePayOfOrderVNPay(String vnp_ResponseCode, Long id) throws CustomException {
        Order order = this.getById(id);
        if (vnp_ResponseCode.equals("00")) {
            order.setPay(OrderConstant.ORDER_PAID);
        } else {
            order.setPay(OrderConstant.ORDER_UNPAID);
        }
        orderRepository.save(order);

    }

    @Transactional
    @Override
    public void updatePayOfOrderPayPal(String approved, Long id) throws CustomException {
        Order order = this.getById(id);
        if (approved.equals("approved")) {
            order.setPay(OrderConstant.ORDER_PAID);
        } else {
            order.setPay(OrderConstant.ORDER_UNPAID);
        }
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void updateOrderByUser(Long id, OrderRequest orderUpdateRequest) throws CustomException {
        Order orderUpdate = this.getById(id);

        String email = methodUtils.getEmailFromTokenOfUser();
        User user = userService.findUserByEmail(email);

        if (!orderUpdate.getUser().getId().equals(user.getId())) {
            throw new CustomException(
                    "You do not have permission to update this order !!!",
                    HttpStatus.UNAUTHORIZED.value()
            );
        }
        orderMapper.orderRequestToOrder(orderUpdateRequest, orderUpdate);
        orderUpdate.setUpdateAtUser(LocalDateTime.now());
        orderUpdate.setUpdateByUser(email);

        orderRepository.save(orderUpdate);
    }

    @Override
    public ResponseData<Long> totalOrders() {
        Long totalOrders = orderRepository.count();
        ResponseData<Long> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get total Orders success !!!");
        responseData.setResults(totalOrders);

        return responseData;
    }

    @Override
    public ResponseData<Double> revenue() {
        Double revenue = orderRepository.sumTotalPrice();
        ResponseData<Double> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get revenue success !!!");
        responseData.setResults(revenue);

        return responseData;
    }

    @Override
    public ResponseData<List<QuantityByBrandResponse>> quantityProductSoldByBrand() {
        List<QuantityByBrandResponse> quantity = orderLineRepository.quantityProductSoldByBrand();

        ResponseData<List<QuantityByBrandResponse>> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Count quantity product sold by Brand success !!!");
        responseData.setResults(quantity);

        return responseData;
    }

    @Override
    public ResponseData<List<OrderStatisticalByYearResponse>> statisticByYear(int year) {
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

        List<OrderStatisticalByYearResponse> statisticalConvert = statistical.stream()
                .sorted((a, b) -> Integer.compare(a.getMonth(), b.getMonth()))
                .collect(Collectors.toList());

        ResponseData<List<OrderStatisticalByYearResponse>> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get statistic by year success !!!");
        responseData.setResults(statisticalConvert);

        return responseData;
    }

    @Override
    public ResponseData<List<String>> getAllYearInOrder() {
        List<String> years = orderRepository.getAllYearInOrder();
        ResponseData<List<String>> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get all years success !!!");
        responseData.setResults(years);

        return responseData;
    }

    @Override
    public ResponseData<Long> averageOrdersValue() {
        long average = Math.round(orderRepository.averageOrdersValue());

        ResponseData<Long> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Calculate average orders value success !!!");
        responseData.setResults(average);

        return responseData;
    }

    @Override
    public ResponseData<Long> sold() {
        long total = orderLineRepository.sold();

        ResponseData<Long> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get total products sold success !!!");
        responseData.setResults(total);

        return responseData;
    }
}
