package com.example.request;

import com.example.Entity.Size;

import java.util.List;
import java.util.Set;

public class ProductRequest {
    private String name;
    private String title;
    private String description;
    private Long brandId;
    private Long parentCategoryId;
    private Long childCategoryId;
    private int discountedPercent;
    private double price;
    private String color;
    private Set<Size> sizes;

    private String mainImageBase64;

    private List<String> imageSecondaries;

    public ProductRequest() {
    }

    public ProductRequest(String name, String title, String description, Long brandId,
                          Long parentCategoryId, Long childCategoryId, int discountedPercent,
                          double price, String color, Set<Size> sizes, String mainImageBase64,
                          List<String> imageSecondaries) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.brandId = brandId;
        this.parentCategoryId = parentCategoryId;
        this.childCategoryId = childCategoryId;
        this.discountedPercent = discountedPercent;
        this.price = price;
        this.color = color;
        this.sizes = sizes;
        this.mainImageBase64 = mainImageBase64;
        this.imageSecondaries = imageSecondaries;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getDiscountedPercent() {
        return discountedPercent;
    }

    public void setDiscountedPercent(int discountedPercent) {
        this.discountedPercent = discountedPercent;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<Size> getSizes() {
        return sizes;
    }

    public void setSizes(Set<Size> sizes) {
        this.sizes = sizes;
    }

    public String getMainImageBase64() {
        return mainImageBase64;
    }

    public void setMainImageBase64(String mainImageBase64) {
        this.mainImageBase64 = mainImageBase64;
    }

    public List<String> getImageSecondaries() {
        return imageSecondaries;
    }

    public void setImageSecondaries(List<String> imageSecondaries) {
        this.imageSecondaries = imageSecondaries;
    }

    @Override
    public String toString() {
        return "ProductRequest{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", brandId=" + brandId +
                ", parentCategoryId=" + parentCategoryId +
                ", childCategoryId=" + childCategoryId +
                ", discountedPercent=" + discountedPercent +
                ", price=" + price +
                ", color='" + color + '\'' +
                ", sizes=" + sizes +
                ", mainImageBase64='" + mainImageBase64 + '\'' +
                ", imageSecondaries=" + imageSecondaries +
                '}';
    }
}
