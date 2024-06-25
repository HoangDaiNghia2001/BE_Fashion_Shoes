package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(name = "code")
    private String code;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "alternate_number")
    private String alternatePhoneNumber;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Column(name = "province")
    private String province;

    @Column(name = "district")
    private String district;

    @Column(name = "ward")
    private String ward;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "status")
    private String status;

    @Column(name = "transport_fee")
    private double transportFee;

    @Column(name = "update_by_user")
    private String updateByUser;

    @Column(name = "update_at_user")
    private LocalDateTime updateAtUser;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "receiving_date")
    private LocalDateTime receivingDate;

    @Column(name = "pay")
    private String pay;

    @Column(name = "note", columnDefinition = "LONGTEXT")
    private String note;

    @Column(name = "payment_method")
    private String paymentMethod;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<OrderLine> orderLines;

    @OneToOne(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private VNPayInformation vnPayInformation;
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAlternatePhoneNumber() {
        return alternatePhoneNumber;
    }

    public void setAlternatePhoneNumber(String alternatePhoneNumber) {
        this.alternatePhoneNumber = alternatePhoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTransportFee() {
        return transportFee;
    }

    public void setTransportFee(double transportFee) {
        this.transportFee = transportFee;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(Set<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getReceivingDate() {
        return receivingDate;
    }

    public void setReceivingDate(LocalDateTime receivingDate) {
        this.receivingDate = receivingDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Order() {
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getUpdateByUser() {
        return updateByUser;
    }

    public void setUpdateByUser(String updateByUser) {
        this.updateByUser = updateByUser;
    }

    public LocalDateTime getUpdateAtUser() {
        return updateAtUser;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setUpdateAtUser(LocalDateTime updateAtUser) {
        this.updateAtUser = updateAtUser;
    }

    public Order(String code, String fullName, String phoneNumber, String alternatePhoneNumber,
                 String address, String province, String district, String ward, double totalPrice,
                 String status, double transportFee, String updateByUser, LocalDateTime updateAtUser,
                 LocalDateTime orderDate, LocalDateTime deliveryDate, LocalDateTime receivingDate,
                 String pay, String note, String paymentMethod, User user, Set<OrderLine> orderLines) {
        this.code = code;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.alternatePhoneNumber = alternatePhoneNumber;
        this.address = address;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.totalPrice = totalPrice;
        this.status = status;
        this.transportFee = transportFee;
        this.updateByUser = updateByUser;
        this.updateAtUser = updateAtUser;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.receivingDate = receivingDate;
        this.pay = pay;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.user = user;
        this.orderLines = orderLines;
    }
}
