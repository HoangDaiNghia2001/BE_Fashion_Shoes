package com.example.request;

public class OtpRequest {
    private String otp;

    public OtpRequest(String otp) {
        this.otp = otp;
    }

    public OtpRequest() {
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
