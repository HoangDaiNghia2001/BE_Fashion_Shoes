package com.example.response;

public class TopBestSellerResponse {
    private Long id;
    private String name;
    private int quantity;
    private long totalPrice;
    private Long sold;

    public TopBestSellerResponse() {
    }

    public TopBestSellerResponse(Long id, String name, int quantity, double totalPrice, Long sold) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.totalPrice = Math.round(totalPrice);
        this.sold = sold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getSold() {
        return sold;
    }

    public void setSold(Long sold) {
        this.sold = sold;
    }
}
