package com.example.api.user;

import com.example.Entity.VNPayInformation;
import com.example.exception.CustomException;
import com.example.request.OrderRequest;
import com.example.response.*;
import com.example.service.OrderService;
import com.example.service.PayPalService;
import com.example.service.VNPayService;
import com.example.util.MethodUtils;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController("orderUser")
@RequestMapping("/api/user")
public class ApiOrder {
    @Autowired
    private OrderService orderDetailService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private PayPalService payPalService;

    private Logger log = LoggerFactory.getLogger(getClass());

    // CALL SUCCESS
    @GetMapping("/order/newest")
    public ResponseEntity<?> getOrderIdNewest() throws CustomException {
        long id = orderDetailService.findOrderIdNewest();

        ResponseData<Long> responseData = new ResponseData<>();
        responseData.setMessage("Get order id newest success !!!");
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setResults(id);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/order/detail")
    public ResponseEntity<?> getOrderDetailById(@RequestParam("id") Long orderId) throws CustomException {
        OrderResponse orderResponse = orderDetailService.getOrderDetail(orderId);

        ResponseData<OrderResponse> responseData = new ResponseData<>();
        responseData.setResults(orderResponse);
        responseData.setSuccess(true);
        responseData.setMessage("Get order detail success !!!");

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/orders/detail")
    public ResponseEntity<?> getOrdersDetail(@RequestParam(value = "orderStatus", required = false) String orderStatus,
                                             @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                                             @RequestParam(value = "orderDateStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime orderDateStart,
                                             @RequestParam(value = "orderDateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime orderDateEnd,
                                             @RequestParam(value = "deliveryDateStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deliveryDateStart,
                                             @RequestParam(value = "deliveryDateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deliveryDateEnd,
                                             @RequestParam(value = "receivingDateStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime receivingDateStart,
                                             @RequestParam(value = "receivingDateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime receivingDateEnd,
                                             @RequestParam(value = "orderCode", required = false) String orderCode) throws CustomException {
        ListOrdersResponse orderResponses = orderDetailService.getOrderDetailsByUser(orderStatus, paymentMethod, orderDateStart, orderDateEnd,
                deliveryDateStart, deliveryDateEnd, receivingDateStart, receivingDateEnd, orderCode);

        ResponseData<ListOrdersResponse> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setResults(orderResponses);
        responseData.setMessage("Get all orders of user success !!!");

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PostMapping("/place/order/cod")
    public ResponseEntity<?> placeOrderCOD(@RequestBody OrderRequest orderRequest) throws CustomException, MessagingException {
        orderDetailService.placeOrderCOD(orderRequest);

        Response response = new Response();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Place order success !!!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/place/order/VNPay")
    public ResponseEntity<?> placeOrderVNPay(@RequestParam("totalPrice") long price,
                                             @RequestParam("orderInfo") String orderInfo,
                                             @RequestParam("orderId") String orderId){

        String placeOrderVNPay = orderDetailService.placeOrderVnPay(price, orderInfo, orderId);

        ResponseData<String> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Payment success !!!");
        responseData.setResults(placeOrderVNPay);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/place/order/PayPal")
    public ResponseEntity<?> placeOrderPayPal(@RequestParam("total") Double total,
                                              @RequestParam("orderId") int orderId){
        String cancelUrl = MethodUtils.getBaseURL(request) + "/" + "api/user/pay/cancel";
        String successUrl = MethodUtils.getBaseURL(request) + "/" + "api/user/pay/success?orderId=" + orderId;
        try {
            Payment payment = payPalService.createPayment(
                    total,
                    "USD",
                    "paypal",
                    "sale",
                    "payment description",
                    cancelUrl,
                    successUrl);
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    ResponseData<String> response = new ResponseData<>();
                    response.setStatus(HttpStatus.OK.value());
                    response.setMessage("Get URL PayPal success !!!");
                    response.setResults(links.getHref());
                    return new ResponseEntity<>(response, HttpStatus.FOUND);
                }
            }
        } catch (PayPalRESTException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @GetMapping("/pay/success")
    public ResponseEntity<?> paymentPayPalSuccess() throws PayPalRESTException, IOException, CustomException, MessagingException {
        String paymentId = request.getParameter("paymentId");
        String payerId = request.getParameter("PayerID");
        Long orderId = Long.valueOf(request.getParameter("orderId"));

        Payment payment = payPalService.executePayment(paymentId, payerId);
        orderDetailService.updatePayOfOrderPayPal(payment.getState(), orderId);
        OrderResponse orderResponse = orderDetailService.getOrderNewestByEmail();
        response.sendRedirect("http://localhost:3000/orders");
        Response response = new Response();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Paypal success !!!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/pay/cancel")
    public ResponseEntity<?> paymentPayPalCancel() throws IOException {
        response.sendRedirect("http://localhost:3000/cart");

        return ResponseEntity.ok().body("Paypal cancel !!!");
    }

    @GetMapping("/payment/paypal/error")
    public ResponseEntity<?> paymentPayPalError() throws IOException {
        response.sendRedirect("http://localhost:3000/");

        Response response = new Response();
        response.setSuccess(false);
        response.setMessage("Paypal error !!!");
        return new ResponseEntity<>(response, HttpStatus.FAILED_DEPENDENCY);
    }

    // CALL SUCCESS
    @DeleteMapping("/order")
    public ResponseEntity<?> cancelOrderByUser(@RequestParam("id") Long idOrder) throws CustomException {
        orderDetailService.cancelOrderByUser(idOrder);

        Response response = new Response();
        response.setMessage("This order is cancel success !!!");
        response.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/order")
    public ResponseEntity<?> updateOrderByUser(@RequestParam("id") Long orderId,
                                               @RequestBody OrderRequest orderUpdateRequest) throws CustomException {
        orderDetailService.updateOrderByUser(orderId, orderUpdateRequest);

        Response response = new Response();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Update order success !!!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/order/vnpay")
    public ResponseEntity<?> vnPayResponse() throws IOException, CustomException, MessagingException {
        VNPayResponse vnPayResponse = new VNPayResponse();
        vnPayResponse.setVnp_Amount(request.getParameter("vnp_Amount"));
        vnPayResponse.setVnp_PayDate(request.getParameter("vnp_PayDate"));
        vnPayResponse.setVnp_ResponseCode(request.getParameter("vnp_ResponseCode"));
        vnPayResponse.setVnp_OrderInfo(request.getParameter("vnp_OrderInfo"));
        vnPayResponse.setVnp_BankCode(request.getParameter("vnp_BankCode"));
        vnPayResponse.setVnp_TransactionNo(request.getParameter("vnp_TransactionNo"));

        List<String> list = List.of(vnPayResponse.getVnp_OrderInfo().split("-"));

        String orderId = list.get(list.size() - 1);

        vnPayService.createVNPayOfOrder(vnPayResponse, Long.valueOf(orderId));

        orderDetailService.updatePayOfOrderVNPay(vnPayResponse.getVnp_ResponseCode(), Long.valueOf(orderId));

        response.sendRedirect("http://localhost:3000/vnpay-response/" + orderId);
//        response.sendRedirect("https://fashion-shoes.vercel.app/vnpay-response/" + orderId);

        return ResponseEntity.ok().body("VNPay response !!!");
    }

    // CALL SUCCESS
    @GetMapping("/order/vnpay/information")
    public ResponseEntity<?> getVNPayInformationByOrderId(@RequestParam("orderId") Long orderId) {
        VNPayInformation vnPayInformation = vnPayService.getVNPayInformationByOrderId(orderId);

        ResponseData<VNPayInformation> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setMessage("Get information of VNPay success !!!");
        responseData.setResults(vnPayInformation);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
