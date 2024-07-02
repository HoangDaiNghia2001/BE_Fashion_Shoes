package com.example.service;

import com.example.Entity.Order;
import com.example.exception.CustomException;
import com.example.request.OrderRequest;
import com.example.request.OrderUpdateRequest;
import com.example.response.ListOrdersResponse;
import com.example.response.OrderResponse;
import com.example.response.OrderStatisticalByYearResponse;
import com.example.response.QuantityByBrandResponse;
import jakarta.mail.MessagingException;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    void placeOrderCOD(OrderRequest orderRequest) throws CustomException, MessagingException;

    String placeOrderVnPay(long totalPrice, String orderInfo, String orderId);

    List<OrderResponse> getOrdersResponse(List<Order> orders);

    List<OrderResponse> getOrderDetailsByUser(String orderStatus, String paymentMethod,
                                              LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                              LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                              LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd) throws CustomException;

    ListOrdersResponse getAllOrderDetailByAdmin(String orderBy, String phoneNumber, String orderStatus, String paymentMethod,
                                                String province, String district, String ward,
                                                LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                                LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                                LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd,
                                                int pageIndex, int pageSize);

    OrderResponse getOrderDetail(Long orderId) throws CustomException;

    void cancelOrderByUser(Long idOrder) throws CustomException;

    void markOrderShipped(Long id) throws CustomException;

    void markOrderConfirmed(Long id) throws CustomException;

    void markOrderDelivered(Long id) throws CustomException;

    void deleteOrderByAdmin(Long id) throws CustomException;

    void deleteSomeOrdersByAdmin(List<Long> listIdOrder) throws CustomException;

    long findOrderIdNewest();

    OrderResponse getOrderNewestByEmail();

    void updatePayOfOrderVNPay(String vnp_ResponseCode, Long orderId) throws CustomException;

    void updatePayOfOrderPayPal(String approved, Long orderId) throws CustomException;

    void updateOrderByUser(Long orderId, OrderUpdateRequest orderUpdateRequest) throws CustomException;

    Long totalOrders();

    Double revenue();

    List<QuantityByBrandResponse> quantityProductSoldByBrand();

    List<OrderStatisticalByYearResponse> statisticByYear(int year);

    List<String> getAllYearInOrder();

    long averageOrdersValue();

    long sold();
}
