package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.dao.OrderDAO;
import com.srihari.sriharimart.dao.UserDAO;
import com.srihari.sriharimart.listener.AppContextListener;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.service.ProductService;
import com.srihari.sriharimart.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Note: pulls UserDAO/OrderDAO directly from the DataSource rather than via a service,
 * since findAll()-style admin queries don't warrant their own service layer for this MVP.
 */
@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.ADMIN)) return;

        String pathInfo = req.getPathInfo();
        ProductService productService = (ProductService) getServletContext().getAttribute(AppContextListener.PRODUCT_SERVICE);

        if (pathInfo == null || pathInfo.equals("/")) {
            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);

        } else if (pathInfo.equals("/users")) {
            UserDAO userDAO = new com.sriram.srirammart.dao.impl.UserDAOImpl(
                    (javax.sql.DataSource) getServletContext().getAttribute(AppContextListener.DATA_SOURCE));
            req.setAttribute("users", userDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/users.jsp").forward(req, resp);

        } else if (pathInfo.equals("/orders")) {
            OrderDAO orderDAO = new com.sriram.srirammart.dao.impl.OrderDAOImpl(
                    (javax.sql.DataSource) getServletContext().getAttribute(AppContextListener.DATA_SOURCE));
            req.setAttribute("orders", orderDAO.findAll());
            req.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(req, resp);

        } else if (pathInfo.equals("/products")) {
            req.setAttribute("products", productService.getAllProducts());
            req.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(req, resp);

        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.ADMIN)) return;

        if ("/products/delete".equals(req.getPathInfo())) {
            ProductService productService = (ProductService) getServletContext().getAttribute(AppContextListener.PRODUCT_SERVICE);
            long id = Long.parseLong(req.getParameter("id"));
            productService.deleteProduct(id);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}