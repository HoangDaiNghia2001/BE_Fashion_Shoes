package com.example.repository;

import com.example.Entity.Order;
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
            "(?8 is null and ?9 is null or o.receivingDate between ?8 and ?9)" +
            "order by o.id desc ")
    List<Order> getOrdersByUser(long idUser, String orderStatus, String paymentMethod,
                                LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd);


    @Query("select o.id from Order o order by o.id desc limit 1")
    long getOrderIdNewest();
}
