package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.listener.AppContextListener;
import com.srihari.sriharimart.model.Product;
import com.srihari.sriharimart.service.ProductService;
import com.srihari.sriharimart.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/products/*")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ProductService productService = (ProductService) getServletContext().getAttribute(AppContextListener.PRODUCT_SERVICE);
        ReviewService reviewService = (ReviewService) getServletContext().getAttribute(AppContextListener.REVIEW_SERVICE);

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String category = req.getParameter("category");
            String keyword = req.getParameter("keyword");

            List<Product> products = (isBlank(category) && isBlank(keyword))
                    ? productService.getAllProducts()
                    : productService.search(category, keyword);

            req.setAttribute("products", products);
            req.getRequestDispatcher("/WEB-INF/views/products/list.jsp").forward(req, resp);

        } else if (pathInfo.equals("/view")) {
            long id = Long.parseLong(req.getParameter("id"));
            Product product = productService.getProductById(id);

            if (product == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            req.setAttribute("product", product);
            req.setAttribute("reviews", reviewService.getReviews(id));
            req.setAttribute("avgRating", reviewService.getAverageRating(id));
            req.getRequestDispatcher("/WEB-INF/views/products/view.jsp").forward(req, resp);

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}