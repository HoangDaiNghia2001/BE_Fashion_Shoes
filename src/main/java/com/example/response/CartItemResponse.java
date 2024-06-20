package com.example.response;

import com.example.Entity.Size;

import java.util.List;
import java.util.Set;

public class CartItemResponse {
    private Long id;
    private Long idProduct;
    private String nameProduct;
    private String titleProduct;
    private double totalPrice;
    private int size;
    private int quantity;
    private String mainImageBase64;
    private String color;
    private Set<Size> sizeProduct;

    public CartItemResponse() {
    }

    public CartItemResponse(Long id, Long idProduct, String nameProduct, String titleProduct,
                            double totalPrice, int size, int quantity, String mainImageBase64, String color, Set<Size> sizeProduct) {
        this.id = id;
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.titleProduct = titleProduct;
        this.totalPrice = totalPrice;
        this.size = size;
        this.quantity = quantity;
        this.mainImageBase64 = mainImageBase64;
        this.color = color;
        this.sizeProduct = sizeProduct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getTitleProduct() {
        return titleProduct;
    }

    public void setTitleProduct(String titleProduct) {
        this.titleProduct = titleProduct;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMainImageBase64() {
        return mainImageBase64;
    }

    public void setMainImageBase64(String mainImageBase64) {
        this.mainImageBase64 = mainImageBase64;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<Size> getSizeProduct() {
        return sizeProduct;
    }

    public void setSizeProduct(Set<Size> sizeProduct) {
        this.sizeProduct = sizeProduct;
    }
}
