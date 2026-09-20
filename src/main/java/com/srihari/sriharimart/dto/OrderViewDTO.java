package com.srihari.sriharimart.dto;

import com.srihari.sriharimart.model.Order;
import java.util.List;

public class OrderViewDTO {
    private final Order order;
    private final List<OrderItemLineDTO> items;

    public OrderViewDTO(Order order, List<OrderItemLineDTO> items) {
        this.order = order;
        this.items = items;
    }

    public Order getOrder() { return order; }
    public List<OrderItemLineDTO> getItems() { return items; }
}