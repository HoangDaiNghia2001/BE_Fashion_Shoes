package com.example.repository;

import com.example.Entity.Order;
import com.example.response.OrderStatisticalByYearResponse;
import com.example.response.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);

    List<Order> findByUserIdOrderByIdDesc(Long userId);

    @Query("select o from Order o where (?1 is null or o.createdBy like %?1% or o.fullName like %?1%) and" +
            "(?2 is null or o.phoneNumber = ?2) and" +
            "(?3 is null or o.status = ?3) and" +
            "(?4 is null or o.paymentMethod = ?4) and " +
            "(?5 is null or o.province = ?5) and" +
            "(?6 is null or o.district = ?6) and" +
            "(?7 is null or o.ward = ?7) and" +
            "(?8 is null and ?9 is null or o.orderDate between ?8 and ?9) and" +
            "(?10 is null and ?11 is null or o.deliveryDate between ?10 and ?11) and " +
            "(?12 is null and ?13 is null or o.receivingDate between ?12 and ?13)" +
            "order by o.id desc ")
    List<Order> getOrdersByAdmin(String orderBy, String phoneNumber, String orderStatus, String paymentMethod,
                                 String province, String district, String ward,
                                 LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                 LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                 LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd);

    @Query("select o from Order o where (o.user.id = ?1) and" +
            "(?2 is null or o.status = ?2) and" +
            "(?3 is null or o.paymentMethod = ?3) and" +
            "(?4 is null and ?5 is null or o.orderDate between ?4 and ?5) and" +
            "(?6 is null and ?7 is null or o.deliveryDate between ?6 and ?7) and " +
            "(?8 is null and ?9 is null or o.receivingDate between ?8 and ?9) and " +
            "(?10 is null or o.code = ?10)" +
            "order by o.id desc ")
    List<Order> getOrdersByUser(long idUser, String orderStatus, String paymentMethod,
                                LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd, String orderCode);


    @Query("select sum(o.totalPrice) from Order o where o.pay = 'PAID'")
    Double sumTotalPrice();

    @Query("select o from Order o where o.createdBy = ?1 order by o.id desc limit 1")
    Order getOrderIdNewest(String email);

    @Query("select new com.example.response.OrderStatisticalByYearResponse(month (o.orderDate), sum(o.totalPrice)) " +
            "from Order o " +
            "where year (o.orderDate)= ?1 " +
            "group by month (o.orderDate)")
    List<OrderStatisticalByYearResponse> statisticByYear(int year);

    @Query("select distinct year (o.createdAt) from Order o")
    List<String> getAllYearInOrder();

    @Query("select avg(o.totalPrice) from Order o")
    double averageOrdersValue();

    Order findByCode(String code);
}
