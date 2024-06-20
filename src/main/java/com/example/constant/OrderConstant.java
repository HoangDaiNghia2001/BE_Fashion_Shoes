package com.example.constant;

import org.springframework.stereotype.Component;

@Component
public class OrderConstant {
    public static final String ORDER_PENDING = "PENDING";
    public static final String ORDER_CONFIRMED = "CONFIRMED";
    public static final String ORDER_SHIPPED = "SHIPPED";
    public static final String ORDER_DELIVERED = "DELIVERED";

    public static final String ORDER_PAID = "PAID";

    public static final String ORDER_UNPAID = "UNPAID";

}
