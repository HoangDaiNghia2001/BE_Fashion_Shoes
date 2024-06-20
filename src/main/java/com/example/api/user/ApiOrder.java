package com.example.api.user;

import com.example.Entity.Order;
import com.example.Entity.VNPayInformation;
import com.example.exception.CustomException;
import com.example.request.OrderRequest;
import com.example.request.OrderUpdateRequest;
import com.example.response.OrderResponse;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.response.VNPayResponse;
import com.example.service.OrderService;
import com.example.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

@RestController("orderUser")
@RequestMapping("/api/user")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiOrder {
    @Autowired
    private OrderService orderDetailService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private VNPayService vnPayService;

    // CALL SUCCESS
    @GetMapping("/order/newest")
    public ResponseEntity<?> getOrderIdNewest() {
        long id = orderDetailService.findOrderIdNewest();
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/order/detail")
    public ResponseEntity<?> getOrderDetailById(@RequestParam("id")Long orderId) throws CustomException {
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
                                            @RequestParam(value = "receivingDateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime receivingDateEnd) throws CustomException {
        List<OrderResponse> orderResponses = orderDetailService.getOrderDetailsByUser(orderStatus, paymentMethod, orderDateStart, orderDateEnd,
                deliveryDateStart, deliveryDateEnd, receivingDateStart, receivingDateEnd);

        ResponseData<List<OrderResponse>> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setResults(orderResponses);
        responseData.setMessage("Get all orders of user success !!!");

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PostMapping("/place/order/cod")
    public ResponseEntity<?> placeOrderCOD(@RequestBody OrderRequest orderRequest) throws CustomException {
        orderDetailService.placeOrderCOD(orderRequest);

        Response response = new Response();
        response.setSuccess(true);
        response.setMessage("Place order success !!!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/place/order/VNPay")
    public ResponseEntity<?> placeOrderVNPay(@RequestParam("totalPrice") long price,
                                             @RequestParam("orderInfo") String orderInfo,
                                             @RequestParam("orderId") String orderId) throws UnsupportedEncodingException {

        String placeOrderVNPay = orderDetailService.placeOrderVnPay(price, orderInfo, orderId);

        ResponseData<String> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMessage("Payment success !!!");
        responseData.setResults(placeOrderVNPay);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/order")
    public ResponseEntity<?> cancelOrderByUser(@RequestParam("id") Long idOrder) throws CustomException {
        orderDetailService.cancelOrderByUser(idOrder);

        Response response = new Response();
        response.setMessage("This order is cancel success !!!");
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/order")
    public ResponseEntity<?> updateOrderByUser(@RequestParam("id")Long orderId,
                                               @RequestBody OrderUpdateRequest orderUpdateRequest) throws CustomException {
        orderDetailService.updateOrderByUser(orderId,orderUpdateRequest);

        Response response = new Response();
        response.setSuccess(true);
        response.setMessage("Update order success !!!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @GetMapping("/order/vnpay")
    public ResponseEntity<?> vnPayResponse() throws IOException, CustomException {
        VNPayResponse vnPayResponse = new VNPayResponse();
        vnPayResponse.setVnp_Amount(request.getParameter("vnp_Amount"));
        vnPayResponse.setVnp_PayDate(request.getParameter("vnp_PayDate"));
        vnPayResponse.setVnp_ResponseCode(request.getParameter("vnp_ResponseCode"));
        vnPayResponse.setVnp_OrderInfo(request.getParameter("vnp_OrderInfo"));
        vnPayResponse.setVnp_BankCode(request.getParameter("vnp_BankCode"));
        vnPayResponse.setVnp_TransactionNo(request.getParameter("vnp_TransactionNo"));

        List<String> list = List.of(vnPayResponse.getVnp_OrderInfo().split("-"));

        String orderId = list.get(list.size()-1);

        vnPayService.createVNPayOfOrder(vnPayResponse, Long.valueOf(orderId));

        orderDetailService.updatePayOfOrderVNPay(vnPayResponse.getVnp_ResponseCode(), Long.valueOf(orderId));

//        response.sendRedirect("http://localhost:3000/vnpay-response/" + orderId);
        response.sendRedirect("https://fashion-shoes.vercel.app/vnpay-response/" + orderId);


        return ResponseEntity.ok().body("VNPay response !!!");
    }

    // CALL SUCCESS
    @GetMapping("/order/vnpay/information")
    public ResponseEntity<?> getVNPayInformationByOrderId(@RequestParam("orderId") Long orderId){
        VNPayInformation vnPayInformation = vnPayService.getVNPayInformationByOrderId(orderId);

        ResponseData<VNPayInformation> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMessage("Get information of VNPay success !!!");
        responseData.setResults(vnPayInformation);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
