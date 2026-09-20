package com.srihari.sriharimart.service;

import com.srihari.sriharimart.dao.OrderDAO;
import com.srihari.sriharimart.model.*;

import java.math.BigDecimal;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO;
    private final ProductService productService;

    public OrderService(OrderDAO orderDAO, ProductService productService) {
        this.orderDAO = orderDAO;
        this.productService = productService;
    }

    public Order placeOrder(long buyerId, Cart cart) {
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Pass 1: validate stock and compute total before writing anything
        for (CartItem cartItem : cart.getItems()) {
            Product product = productService.getProductById(cartItem.getProductId());

            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + cartItem.getProductId());
            }
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        // Pass 2: create the order, then its items, then decrement stock
        Order order = new Order(0, buyerId, totalAmount, OrderStatus.CONFIRMED);
        orderDAO.save(order);

        for (CartItem cartItem : cart.getItems()) {
            Product product = productService.getProductById(cartItem.getProductId());

            OrderItem orderItem = new OrderItem(order.getId(), product.getId(), cartItem.getQuantity(), product.getPrice());
            orderDAO.saveItem(orderItem);

            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productService.updateProduct(product);
        }

        cart.getItems().clear();
        return order;
    }

    public List<Order> getOrdersByBuyer(long buyerId) {
        return orderDAO.findByBuyerId(buyerId);
    }

    public List<Order> getOrdersBySeller(long sellerId) {
        return orderDAO.findBySellerId(sellerId);
    }

    public List<OrderItem> getOrderItems(long orderId) {
        return orderDAO.findItemsByOrderId(orderId);
    }

    public void updateStatus(long orderId, OrderStatus status) {
        orderDAO.updateStatus(orderId, status);
    }
}