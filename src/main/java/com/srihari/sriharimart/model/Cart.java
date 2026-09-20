package com.srihari.sriharimart.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private long id;
    private long buyerId;
    private List<CartItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public Cart(long id, long buyerId, List<CartItem> items) {
    this.id = id;
    this.buyerId = buyerId;
    this.items = items != null ? items : new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(long buyerId) {
        this.buyerId = buyerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}