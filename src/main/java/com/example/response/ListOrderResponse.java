package com.example.response;

import java.util.List;

public class ListOrderResponse {
    private List<OrderResponse> listOrders;
    private Long total;

    public ListOrderResponse() {
    }

    public ListOrderResponse(List<OrderResponse> listOrders, Long total) {
        this.listOrders = listOrders;
        this.total = total;
    }

    public List<OrderResponse> getListOrders() {
        return listOrders;
    }

    public void setListOrders(List<OrderResponse> listOrders) {
        this.listOrders = listOrders;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
