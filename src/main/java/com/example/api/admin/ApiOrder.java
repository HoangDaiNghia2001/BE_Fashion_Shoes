package com.example.api.admin;

import com.example.exception.CustomException;
import com.example.response.*;
import com.example.service.OrderService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("orderOfRoleAdmin")
@RequestMapping("/api/admin")
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
                                              @RequestParam("pageSize") int pageSize) throws CustomException {
        ListOrdersResponse orderResponseList = orderDetailService.getAllOrderDetailByAdmin(orderBy, phoneNumber, orderStatus, paymentMethod, province,
                district, ward, orderDateStart, orderDateEnd, deliveryDateStart,
                deliveryDateEnd, receivingDateStart, receivingDateEnd, pageIndex, pageSize);

        ResponseData<ListOrdersResponse> responseData = new ResponseData<>();
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setResults(orderResponseList);
        responseData.setMessage("Filter orders success !!!");

        return new ResponseEntity<>(responseData, HttpStatus.OK);

    }

    // CALL SUCCESS
    @PutMapping("/order/confirmed")
    public ResponseEntity<?> confirmedOrder(@RequestParam("id") Long id) throws CustomException, MessagingException {
        orderDetailService.markOrderConfirmed(id);

        Response response = new Response();
        response.setMessage("Confirmed order success !!!");
        response.setStatus(HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/order/shipped")
    public ResponseEntity<?> shippedOrder(@RequestParam("id") Long id) throws CustomException, MessagingException {
        orderDetailService.markOrderShipped(id);

        Response response = new Response();
        response.setMessage("Order is being shipped !!!");
        response.setStatus(HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PutMapping("/order/delivered")
    public ResponseEntity<?> deliveredOrder(@RequestParam("id") Long id) throws CustomException, MessagingException {
        orderDetailService.markOrderDelivered(id);

        Response response = new Response();
        response.setMessage("The order was delivered successfully !!!");
        response.setStatus(HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/order")
    public ResponseEntity<?> deleteOrder(@RequestParam("id") Long id) throws CustomException, MessagingException {
        Response response = orderDetailService.deleteOrderByAdmin(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //CALL SUCCESS
    @DeleteMapping("/orders/{listIdOrders}")
    public ResponseEntity<?> deleteSomeOrders(@PathVariable List<Long> listIdOrders) throws CustomException, MessagingException {
        Response response = orderDetailService.deleteSomeOrdersByAdmin(listIdOrders);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/orders/total")
    public ResponseEntity<?> totalOrders() {
        ResponseData<Long> responseData = orderDetailService.totalOrders();
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/orders/revenue")
    public ResponseEntity<?> revenue() {
        ResponseData<Double> responseData = orderDetailService.revenue();
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/orders/product/quantity/sold")
    public ResponseEntity<?> quantitySoldByBrand() {
        ResponseData<List<QuantityByBrandResponse>> responseData = orderDetailService.quantityProductSoldByBrand();
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/orders/statistic/year/{year}")
    public ResponseEntity<?> statisticalByYear(@PathVariable("year") int year) {
        ResponseData<List<OrderStatisticalByYearResponse>> responseData = orderDetailService.statisticByYear(year);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/orders/years")
    public ResponseEntity<?> getAllYearInOrder() {
        ResponseData<List<String>> responseData = orderDetailService.getAllYearInOrder();
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/orders/average/orders/value")
    public ResponseEntity<?> averageOrdersValue() {
        ResponseData<Long> responseData = orderDetailService.averageOrdersValue();
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/products/sold")
    public ResponseEntity<?> sold() {
        ResponseData<Long> responseData = orderDetailService.sold();
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
