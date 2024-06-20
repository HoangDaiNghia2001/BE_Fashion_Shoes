package com.example.request;

import java.util.List;

public class OrderRequest {
    private String fullName;
    private String address;
    private String district;
    private String province;
    private String ward;
    private String phoneNumber;
    private String alternatePhoneNumber;
    private String paymentMethod;
    private String note;
    private double transportFee;
    private List<OrderProductQuantityRequest> productQuantities;

    public OrderRequest() {
    }

    public OrderRequest(String fullName, String address, String district, String province, String ward, String phoneNumber,
                        String alternatePhoneNumber, String paymentMethod,
                        String note, double transportFee, List<OrderProductQuantityRequest> productQuantities) {
        this.fullName = fullName;
        this.address = address;
        this.district = district;
        this.province = province;
        this.ward = ward;
        this.phoneNumber = phoneNumber;
        this.alternatePhoneNumber = alternatePhoneNumber;
        this.paymentMethod = paymentMethod;
        this.note = note;
        this.transportFee = transportFee;
        this.productQuantities = productQuantities;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTransportFee() {
        return transportFee;
    }

    public void setTransportFee(double transportFee) {
        this.transportFee = transportFee;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
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

    public List<OrderProductQuantityRequest> getProductQuantities() {
        return productQuantities;
    }

    public void setProductQuantities(List<OrderProductQuantityRequest> productQuantities) {
        this.productQuantities = productQuantities;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
