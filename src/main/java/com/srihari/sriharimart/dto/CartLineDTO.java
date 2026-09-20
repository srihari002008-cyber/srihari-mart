package com.srihari.sriharimart.dto;

import com.srihari.sriharimart.model.Product;
import java.math.BigDecimal;

public class CartLineDTO {
    private final Product product;
    private final int quantity;
    private final BigDecimal lineTotal;

    public CartLineDTO(Product product, int quantity, BigDecimal lineTotal) {
        this.product = product;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public BigDecimal getLineTotal() { return lineTotal; }
}