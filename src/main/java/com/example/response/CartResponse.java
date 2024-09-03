package com.example.response;

import java.util.List;

public class CartResponse {
    private List<CartItemResponse> listCartItems;

    private Long totalItems;

    public CartResponse() {
    }

    public CartResponse(List<CartItemResponse> listCartItems, Long totalItems) {
        this.listCartItems = listCartItems;
        this.totalItems = totalItems;
    }

    public List<CartItemResponse> getListCartItems() {
        return listCartItems;
    }

    public void setListCartItems(List<CartItemResponse> listCartItems) {
        this.listCartItems = listCartItems;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }
}
