package com.srihari.sriharimart.dao;

import com.srihari.sriharimart.model.Order;
import com.srihari.sriharimart.model.OrderItem;
import com.srihari.sriharimart.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    void save(Order order);
    void saveItem(OrderItem item);
    Optional<Order> findById(long id);
    List<Order> findByBuyerId(long buyerId);
    List<Order> findBySellerId(long sellerId);
    List<Order> findAll();
    List<OrderItem> findItemsByOrderId(long orderId);
    void updateStatus(long orderId, OrderStatus status);
    boolean existsPurchase(long buyerId, long productId);
}