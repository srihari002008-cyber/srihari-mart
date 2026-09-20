package com.srihari.sriharimart.dto;

import java.math.BigDecimal;

public class OrderItemLineDTO {
    private final String productName;
    private final int quantity;
    private final BigDecimal price;

    public OrderItemLineDTO(String productName, int quantity, BigDecimal price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
}