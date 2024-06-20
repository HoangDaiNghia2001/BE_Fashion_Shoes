package com.example.request;

public class ParentCategoryRequest {
    private String name;
    private Long brandId;

    public ParentCategoryRequest() {
    }

    public ParentCategoryRequest(String name, Long brandId) {
        this.name = name;
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }
}
