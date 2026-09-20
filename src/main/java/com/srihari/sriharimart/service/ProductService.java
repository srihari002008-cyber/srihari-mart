package com.srihari.sriharimart.service;

import com.srihari.sriharimart.dao.ProductDAO;
import com.srihari.sriharimart.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product createProduct(long sellerId, String name, String description,
                                  BigDecimal price, int quantity, String category, String imageUrl) {

        validateProductDetails(name, price, quantity);

        Product product = new Product(0, sellerId, name, description, price, quantity, category, imageUrl);
        productDAO.save(product);
        return product;
    }

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public List<Product> getProductsBySeller(long sellerId) {
        return productDAO.findBySellerId(sellerId);
    }

    public List<Product> search(String category, String keyword) {
        return productDAO.search(category, keyword);
    }

    public Product getProductById(long id) {
        return productDAO.findById(id).orElse(null);
    }

    public void updateProduct(Product product) {
        validateProductDetails(product.getName(), product.getPrice(), product.getQuantity());
        productDAO.update(product);
    }

    public void deleteProduct(long id) {
        Optional<Product> product = productDAO.findById(id);
        if (product.isEmpty()) throw new IllegalArgumentException("Product not found");
        productDAO.delete(id);
    }

    private void validateProductDetails(String name, BigDecimal price, int quantity) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Product name cannot be empty");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
    }
}