package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.listener.AppContextListener;
import com.srihari.sriharimart.model.Product;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.service.ProductService;
import com.srihari.sriharimart.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/seller/products/*")
public class SellerProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.SELLER)) return;

        ProductService productService = (ProductService) getServletContext().getAttribute(AppContextListener.PRODUCT_SERVICE);
        long sellerId = SessionUtil.getUserId(req);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            req.setAttribute("products", productService.getProductsBySeller(sellerId));
            req.getRequestDispatcher("/WEB-INF/views/seller/products.jsp").forward(req, resp);

        } else if (pathInfo.equals("/edit")) {
            long id = Long.parseLong(req.getParameter("id"));
            Product product = productService.getProductById(id);

            if (product == null || product.getSellerId() != sellerId) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            req.setAttribute("product", product);
            req.getRequestDispatcher("/WEB-INF/views/seller/product-edit.jsp").forward(req, resp);

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.SELLER)) return;

        ProductService productService = (ProductService) getServletContext().getAttribute(AppContextListener.PRODUCT_SERVICE);
        long sellerId = SessionUtil.getUserId(req);
        String pathInfo = req.getPathInfo();

        try {
            if ("/create".equals(pathInfo)) {
                productService.createProduct(
                        sellerId,
                        req.getParameter("name"),
                        req.getParameter("description"),
                        new BigDecimal(req.getParameter("price")),
                        Integer.parseInt(req.getParameter("quantity")),
                        req.getParameter("category"),
                        req.getParameter("imageUrl")
                );

            } else if ("/update".equals(pathInfo)) {
                long id = Long.parseLong(req.getParameter("id"));
                Product existing = productService.getProductById(id);

                if (existing == null || existing.getSellerId() != sellerId) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                existing.setName(req.getParameter("name"));
                existing.setDescription(req.getParameter("description"));
                existing.setPrice(new BigDecimal(req.getParameter("price")));
                existing.setQuantity(Integer.parseInt(req.getParameter("quantity")));
                existing.setCategory(req.getParameter("category"));
                existing.setImageUrl(req.getParameter("imageUrl"));
                productService.updateProduct(existing);

            } else if ("/delete".equals(pathInfo)) {
                long id = Long.parseLong(req.getParameter("id"));
                Product existing = productService.getProductById(id);

                if (existing == null || existing.getSellerId() != sellerId) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                productService.deleteProduct(id);
            }

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/seller/products");
    }
}