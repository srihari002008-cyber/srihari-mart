package com.srihari.sriharimart.service;

import com.srihari.sriharimart.dao.CartDAO;
import com.srihari.sriharimart.model.Cart;
import com.srihari.sriharimart.model.CartItem;

public class CartService {

    private final CartDAO cartDAO;

    public CartService(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    public Cart getCart(long buyerId) {
        // CartDAOImpl.findByBuyerId always returns Optional.of(...), never empty,
        // so this is safe — an empty cart is just a Cart with no items.
        return cartDAO.findByBuyerId(buyerId).orElse(new Cart(0, buyerId, null));
    }

    public void addItem(long buyerId, long productId, int quantity) {
        validateQuantity(quantity);
        cartDAO.addItem(buyerId, new CartItem(productId, quantity));
    }

    public void updateItem(long buyerId, long productId, int quantity) {
        validateQuantity(quantity);
        cartDAO.updateItem(buyerId, new CartItem(productId, quantity));
    }

    public void removeItem(long buyerId, long productId) {
        cartDAO.removeItem(buyerId, productId);
    }

    public void clearCart(long buyerId) {
        cartDAO.clearCart(buyerId);
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
}