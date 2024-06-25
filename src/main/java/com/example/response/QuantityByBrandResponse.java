package com.example.response;

public class QuantityByBrandResponse {
    private String brandName;
    private Long quantity;

    public QuantityByBrandResponse(String brandName, Long quantity) {
        this.brandName = brandName;
        this.quantity = quantity;
    }

    public QuantityByBrandResponse() {
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
