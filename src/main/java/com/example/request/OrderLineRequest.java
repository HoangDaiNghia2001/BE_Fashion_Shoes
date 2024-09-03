package com.example.request;

public class OrderLineRequest {
    private Long productId;
    private int size;
    private int quantity;

    private Long totalPrice;

    public OrderLineRequest() {
    }

    public OrderLineRequest(Long productId, int size, int quantity, Long totalPrice) {
        this.productId = productId;
        this.size = size;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }
}
