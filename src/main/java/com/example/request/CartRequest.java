package com.example.request;

public class CartRequest {
    private Long productId;
    private int size;
    private int quantity;

    public CartRequest() {
    }

    public CartRequest(Long productId, int size, int quantity) {
        this.productId = productId;
        this.size = size;
        this.quantity = quantity;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
