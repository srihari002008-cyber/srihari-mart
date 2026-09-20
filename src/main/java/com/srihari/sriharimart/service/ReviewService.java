package com.srihari.sriharimart.service;

import com.srihari.sriharimart.dao.OrderDAO;
import com.srihari.sriharimart.dao.ReviewDAO;
import com.srihari.sriharimart.model.Review;

import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO;
    private final OrderDAO orderDAO;

    public ReviewService(ReviewDAO reviewDAO, OrderDAO orderDAO) {
        this.reviewDAO = reviewDAO;
        this.orderDAO = orderDAO;
    }

    /** F8: only buyers who actually bought the product can review it. */
    public void addReview(long buyerId, long productId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (!orderDAO.existsPurchase(buyerId, productId)) {
            throw new IllegalArgumentException("You can only review products you have purchased");
        }

        Review review = new Review(0, productId, buyerId, rating, comment, null);
        reviewDAO.save(review);
    }

    public List<Review> getReviews(long productId) {
        return reviewDAO.findByProductId(productId);
    }

    public double getAverageRating(long productId) {
        return reviewDAO.findAverageRatingByProductId(productId);
    }
}