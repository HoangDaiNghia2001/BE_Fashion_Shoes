package com.example.service;

import com.example.Entity.Order;
import com.example.exception.CustomException;
import com.example.request.OrderRequest;
import com.example.response.*;
import jakarta.mail.MessagingException;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Order getById(Long id) throws CustomException;
    void placeOrderCOD(OrderRequest orderRequest) throws CustomException, MessagingException;

    String placeOrderVnPay(long totalPrice, String orderInfo, String orderId);

    ListOrdersResponse getOrderDetailsByUser(String orderStatus, String paymentMethod,
                                              LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                              LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                              LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd, String orderCode) throws CustomException;

    ListOrdersResponse getAllOrderDetailByAdmin(String orderBy, String phoneNumber, String orderStatus, String paymentMethod,
                                                String province, String district, String ward,
                                                LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                                LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                                LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd,
                                                int pageIndex, int pageSize) throws CustomException;

    OrderResponse getOrderDetail(Long id) throws CustomException;

    void cancelOrderByUser(Long id) throws CustomException;

    void markOrderShipped(Long id) throws CustomException, MessagingException;

    void markOrderConfirmed(Long id) throws CustomException, MessagingException;

    void markOrderDelivered(Long id) throws CustomException, MessagingException;

    Response deleteOrderByAdmin(Long id) throws CustomException, MessagingException;

    Response deleteSomeOrdersByAdmin(List<Long> listIdOrder) throws CustomException, MessagingException;

    long findOrderIdNewest() throws CustomException;

    OrderResponse getOrderNewestByEmail() throws CustomException;

    void updatePayOfOrderVNPay(String vnp_ResponseCode, Long id) throws CustomException;

    void updatePayOfOrderPayPal(String approved, Long id) throws CustomException;

    void updateOrderByUser(Long id, OrderRequest orderUpdateRequest) throws CustomException;

    ResponseData<Long> totalOrders();

    ResponseData<Double> revenue();

    ResponseData<List<QuantityByBrandResponse>> quantityProductSoldByBrand();

    ResponseData<List<OrderStatisticalByYearResponse>> statisticByYear(int year);

    ResponseData<List<String>> getAllYearInOrder();

    ResponseData<Long> averageOrdersValue();

    ResponseData<Long> sold();

}
