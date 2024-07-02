package com.example.response;

import java.util.List;

public class ListOrdersResponse {
    private List<OrderResponse> listOrders;
    private Long total;

    public ListOrdersResponse() {
    }

    public ListOrdersResponse(List<OrderResponse> listOrders, Long total) {
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
