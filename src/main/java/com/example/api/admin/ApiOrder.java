package com.example.api.admin;

import com.example.exception.CustomException;
import com.example.response.ListOrderResponse;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("orderOfRoleAdmin")
@RequestMapping("/api/admin")
//@CrossOrigin(origins = {"http://localhost:3000/","http://localhost:3001/","https://fashion-shoes.vercel.app/"}, allowCredentials = "true")
public class ApiOrder {
    @Autowired
    private OrderService orderDetailService;

    // CALL SUCCESS
    @GetMapping("/orders")
    public ResponseEntity<?> getOrdersByAdmin(@RequestParam(value = "orderBy", required = false) String orderBy,
                                              @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
                                              @RequestParam(value = "orderStatus", required = false) String orderStatus,
                                              @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                                              @RequestParam(value = "province", required = false) String province,
                                              @RequestParam(value = "district", required = false) String district,
                                              @RequestParam(value = "ward", required = false) String ward,
                                              @RequestParam(value = "orderDateStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime orderDateStart,
                                              @RequestParam(value = "orderDateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime orderDateEnd,
                                              @RequestParam(value = "deliveryDateStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deliveryDateStart,
                                              @RequestParam(value = "deliveryDateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deliveryDateEnd,
                                              @RequestParam(value = "receivingDateStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime receivingDateStart,
                                              @RequestParam(value = "receivingDateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime receivingDateEnd,
                                              @RequestParam("pageIndex") int pageIndex,
                                              @RequestParam("pageSize") int pageSize) {
        ListOrderResponse orderResponseList = orderDetailService.getAllOrderDetailByAdmin(orderBy, phoneNumber, orderStatus, paymentMethod, province,
                district, ward, orderDateStart, orderDateEnd, deliveryDateStart,
                deliveryDateEnd, receivingDateStart, receivingDateEnd, pageIndex, pageSize);

        ResponseData<ListOrderResponse> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setResults(orderResponseList);
        responseData.setMessage("Filter orders success !!!");

        return new ResponseEntity<>(responseData, HttpStatus.OK);

    }

    // CALL SUCCESS
    @PutMapping("/order/confirmed")
    public ResponseEntity<?> confirmedOrder(@RequestParam("id") Long id) throws CustomException {
        orderDetailService.markOrderConfirmed(id);

        Response response = new Response();
        response.setMessage("Confirmed order success !!!");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/order/shipped")
    public ResponseEntity<?> shippedOrder(@RequestParam("id") Long id) throws CustomException {
        orderDetailService.markOrderShipped(id);

        Response response = new Response();
        response.setMessage("Order is being shipped !!!");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/order/delivered")
    public ResponseEntity<?> deliveredOrder(@RequestParam("id") Long id) throws CustomException {
        orderDetailService.markOrderDelivered(id);

        Response response = new Response();
        response.setMessage("The order was delivered successfully !!!");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/order")
    public ResponseEntity<?> deleteOrder(@RequestParam("id") Long id) throws CustomException {
        orderDetailService.deleteOrderByAdmin(id);

        Response response = new Response();
        response.setMessage("Delete order successfully !!!");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //CALL SUCCESS
    @DeleteMapping("/orders/{listIdOrders}")
    public ResponseEntity<?> deleteSomeOrders(@PathVariable List<Long> listIdOrders) throws CustomException {
        orderDetailService.deleteSomeOrdersByAdmin(listIdOrders);

        Response response = new Response();
        response.setMessage("Delete some orders successfully !!!");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
