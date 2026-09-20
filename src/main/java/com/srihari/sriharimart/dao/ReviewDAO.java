package com.srihari.sriharimart.dao;

import com.srihari.sriharimart.model.Review;

import java.util.List;

public interface ReviewDAO {
    void save(Review review);
    List<Review> findByProductId(long productId);
    double findAverageRatingByProductId(long productId);
}