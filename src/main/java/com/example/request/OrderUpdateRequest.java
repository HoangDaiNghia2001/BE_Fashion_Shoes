package com.example.request;

public class OrderUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private String alternatePhone;
    private String notes;
    private String address;
    private String ward;
    private String district;
    private String province;

    public OrderUpdateRequest() {
    }

    public OrderUpdateRequest(String fullName, String phoneNumber, String alternatePhone,
                              String notes, String address, String ward, String district, String province) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.alternatePhone = alternatePhone;
        this.notes = notes;
        this.address = address;
        this.ward = ward;
        this.district = district;
        this.province = province;
    }

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

    public String getAlternatePhone() {
        return alternatePhone;
    }

    public void setAlternatePhone(String alternatePhone) {
        this.alternatePhone = alternatePhone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
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
}
