package com.example.response;

import com.example.Entity.Product;
import com.example.Entity.Size;

import java.util.List;
import java.util.Set;

public class CartItemResponse {
    private Long id;
    private double totalPrice;
    private int size;
    private int quantity;
    private boolean outOffStock;
    private Product product;

    public CartItemResponse() {
    }

    public CartItemResponse(Long id, double totalPrice, int size, int quantity, boolean outOffStock, Product product) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.size = size;
        this.quantity = quantity;
        this.outOffStock = outOffStock;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isOutOffStock() {
        return outOffStock;
    }

    public void setOutOffStock(boolean outOffStock) {
        this.outOffStock = outOffStock;
    }
}
