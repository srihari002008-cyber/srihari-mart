package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.listener.AppContextListener;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.service.ReviewService;
import com.srihari.sriharimart.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/reviews/add")
public class ReviewServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.BUYER)) return;

        ReviewService reviewService = (ReviewService) getServletContext().getAttribute(AppContextListener.REVIEW_SERVICE);
        long buyerId = SessionUtil.getUserId(req);
        long productId = Long.parseLong(req.getParameter("productId"));

        try {
            int rating = Integer.parseInt(req.getParameter("rating"));
            String comment = req.getParameter("comment");
            reviewService.addReview(buyerId, productId, rating, comment);
            resp.sendRedirect(req.getContextPath() + "/products/view?id=" + productId);

        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/products/view?id=" + productId + "&reviewError=1");
        }
    }
}