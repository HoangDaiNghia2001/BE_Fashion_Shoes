package com.example.response;

import java.time.Month;

public class OrderStatisticalByYearResponse {
    private int month;
    private long revenue;

    public OrderStatisticalByYearResponse() {
    }

    public OrderStatisticalByYearResponse(int month, double revenue) {
        this.month = month;
        this.revenue = Math.round(revenue);
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }
}
