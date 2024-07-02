package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product")
public class Product extends BaseEntity {

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private double price;

    @Column(name = "main_image", columnDefinition = "LONGTEXT")
    private String mainImageBase64;

    @Column(name = "discounted_percent")
    private int discountedPercent;

    @Column(name = "discounted_price")
    private double discountedPrice;

    @Column(name = "color")
    private String color;

    @JsonIgnore
    @OneToMany(mappedBy = "productOfComment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brandProduct;

    @ManyToOne
    @JoinColumn(name = "parentCategory_id")
    private ParentCategory parentCategoryOfProduct;

    @ManyToOne
    @JoinColumn(name = "childCategory_id")
    private ChildCategory childCategoryOfProduct;

    @ElementCollection
    @Column(name = "image_secondary", columnDefinition = "LONGTEXT")
    private List<String> imageSecondaries;

    @ElementCollection
    private Set<Size> sizes = new HashSet<>();

    //getter-setter
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDiscountedPercent() {
        return discountedPercent;
    }

    public void setDiscountedPercent(int discountedPercent) {
        this.discountedPercent = discountedPercent;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Brand getBrandProduct() {
        return brandProduct;
    }

    public void setBrandProduct(Brand brandProduct) {
        this.brandProduct = brandProduct;
    }

    public ParentCategory getParentCategoryOfProduct() {
        return parentCategoryOfProduct;
    }

    public void setParentCategoryOfProduct(ParentCategory parentCategoryOfProduct) {
        this.parentCategoryOfProduct = parentCategoryOfProduct;
    }

    public ChildCategory getChildCategoryOfProduct() {
        return childCategoryOfProduct;
    }

    public void setChildCategoryOfProduct(ChildCategory childCategoryOfProduct) {
        this.childCategoryOfProduct = childCategoryOfProduct;
    }

    public Set<Size> getSizes() {
        return sizes;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSizes(Set<Size> sizes) {
        this.sizes = sizes;
    }

    public Product() {
    }

    public Product(String code, String name, String description, String title,
                   int quantity, double price, String mainImageBase64, int discountedPercent,
                   double discountedPrice, String color, Set<Comment> comments, Brand brandProduct,
                   ParentCategory parentCategoryOfProduct, ChildCategory childCategoryOfProduct,
                   List<String> imageSecondaries, Set<Size> sizes) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
        this.mainImageBase64 = mainImageBase64;
        this.discountedPercent = discountedPercent;
        this.discountedPrice = discountedPrice;
        this.color = color;
        this.comments = comments;
        this.brandProduct = brandProduct;
        this.parentCategoryOfProduct = parentCategoryOfProduct;
        this.childCategoryOfProduct = childCategoryOfProduct;
        this.imageSecondaries = imageSecondaries;
        this.sizes = sizes;
    }
}
