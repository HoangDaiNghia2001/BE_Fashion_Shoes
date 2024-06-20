package com.example.response;

public class VNPayResponse {
    private String vnp_ResponseCode;
    private String vnp_PayDate;
    private String vnp_TransactionNo;
    private String vnp_BankCode;
    private String vnp_OrderInfo;
    private String vnp_Amount;
    private String orderId;

    public VNPayResponse() {
    }

    public VNPayResponse(String vnp_ResponseCode, String vnp_PayDate,
                         String vnp_TransactionNo, String vnp_BankCode, String vnp_OrderInfo, String vnp_Amount, String orderId) {
        this.vnp_ResponseCode = vnp_ResponseCode;
        this.vnp_PayDate = vnp_PayDate;
        this.vnp_TransactionNo = vnp_TransactionNo;
        this.vnp_BankCode = vnp_BankCode;
        this.vnp_OrderInfo = vnp_OrderInfo;
        this.vnp_Amount = vnp_Amount;
        this.orderId = orderId;
    }

    public String getVnp_ResponseCode() {
        return vnp_ResponseCode;
    }

    public void setVnp_ResponseCode(String vnp_ResponseCode) {
        this.vnp_ResponseCode = vnp_ResponseCode;
    }

    public String getVnp_PayDate() {
        return vnp_PayDate;
    }

    public void setVnp_PayDate(String vnp_PayDate) {
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

    public String getVnp_Amount() {
        return vnp_Amount;
    }

    public void setVnp_Amount(String vnp_Amount) {
        this.vnp_Amount = vnp_Amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
