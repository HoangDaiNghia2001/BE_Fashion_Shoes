package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "VNPay")
public class VNPayInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "response_code")
    private String vnp_ResponseCode;

    @Column(name = "pay_date")
    private LocalDateTime vnp_PayDate;

    @Column(name = "transaction_id")
    private String vnp_TransactionNo;

    @Column(name = "bank_code")
    private String vnp_BankCode;

    @Column(name = "order_information", columnDefinition = "TEXT")
    private String vnp_OrderInfo;

    @Column(name = "total_price")
    private long vnp_Amount;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private Order order;

    public VNPayInformation() {
    }

    public VNPayInformation(long id, String vnp_ResponseCode, LocalDateTime vnp_PayDate,
                            String vnp_TransactionNo, String vnp_BankCode, String vnp_OrderInfo, long vnp_Amount, Order order) {
        this.id = id;
        this.vnp_ResponseCode = vnp_ResponseCode;
        this.vnp_PayDate = vnp_PayDate;
        this.vnp_TransactionNo = vnp_TransactionNo;
        this.vnp_BankCode = vnp_BankCode;
        this.vnp_OrderInfo = vnp_OrderInfo;
        this.vnp_Amount = vnp_Amount;
        this.order = order;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVnp_ResponseCode() {
        return vnp_ResponseCode;
    }

    public void setVnp_ResponseCode(String vnp_ResponseCode) {
        this.vnp_ResponseCode = vnp_ResponseCode;
    }

    public LocalDateTime getVnp_PayDate() {
        return vnp_PayDate;
    }

    public void setVnp_PayDate(LocalDateTime vnp_PayDate) {
        this.vnp_PayDate = vnp_PayDate;
    }

    public String getVnp_TransactionNo() {
        return vnp_TransactionNo;
    }

    public void setVnp_TransactionNo(String vnp_TransactionNo) {
        this.vnp_TransactionNo = vnp_TransactionNo;
    }

    public String getVnp_BankCode() {
        return vnp_BankCode;
    }

    public void setVnp_BankCode(String vnp_BankCode) {
        this.vnp_BankCode = vnp_BankCode;
    }

    public String getVnp_OrderInfo() {
        return vnp_OrderInfo;
    }

    public void setVnp_OrderInfo(String vnp_OrderInfo) {
        this.vnp_OrderInfo = vnp_OrderInfo;
    }

    public double getVnp_Amount() {
        return vnp_Amount;
    }

    public void setVnp_Amount(long vnp_Amount) {
        this.vnp_Amount = vnp_Amount;
    }

    @JsonIgnore
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
