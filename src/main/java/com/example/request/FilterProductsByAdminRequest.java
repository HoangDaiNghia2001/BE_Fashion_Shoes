package com.example.request;

public class FilterProductsByAdminRequest {
    private String name;
    private Long brandId;
    private Long parentCategoryId;
    private Long childCategoryId;
    private String color;
    private int discountedPercent;
    private String createdBy;
    private String updatedBy;

    public FilterProductsByAdminRequest() {
    }

    public FilterProductsByAdminRequest(String name, Long brandId, Long parentCategoryId,
                                        Long childCategoryId, String color, int discountedPercent, String createdBy, String updatedBy) {
        this.name = name;
        this.brandId = brandId;
        this.parentCategoryId = parentCategoryId;
        this.childCategoryId = childCategoryId;
        this.color = color;
        this.discountedPercent = discountedPercent;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
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

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Long getChildCategoryId() {
        return childCategoryId;
    }

    public void setChildCategoryId(Long childCategoryId) {
        this.childCategoryId = childCategoryId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getDiscountedPercent() {
        return discountedPercent;
    }

    public void setDiscountedPercent(int discountedPercent) {
        this.discountedPercent = discountedPercent;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
