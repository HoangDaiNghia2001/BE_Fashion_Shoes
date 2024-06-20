package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "cart")
public class Cart extends BaseEntity{
    @Column(name = "quantity")
    private int quantity;

    @Column(name = "size")
    private int size;

    @Column(name = "total_price")
    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

//    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Cart(int quantity, int size, double totalPrice, Product product, User user) {
        this.quantity = quantity;
        this.size = size;
        this.totalPrice = totalPrice;
        this.product = product;
        this.user = user;
    }

    public Cart() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
