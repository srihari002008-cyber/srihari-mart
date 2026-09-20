package com.srihari.sriharimart.dao;

import com.srihari.sriharimart.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    void save(Product product);
    Optional<Product> findById(long id);
    List<Product> findAll();
    List<Product> findBySellerId(long sellerId);
    List<Product> search(String category, String keyword);
    void update(Product product);
    void delete(long id);
}