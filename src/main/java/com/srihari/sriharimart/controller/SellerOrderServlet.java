package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.dto.OrderItemLineDTO;
import com.srihari.sriharimart.dto.OrderViewDTO;
import com.srihari.sriharimart.listener.AppContextListener;
import com.srihari.sriharimart.model.*;
import com.srihari.sriharimart.service.OrderService;
import com.srihari.sriharimart.service.ProductService;
import com.srihari.sriharimart.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/seller/orders")
public class SellerOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.SELLER)) return;

        OrderService orderService = (OrderService) getServletContext().getAttribute(AppContextListener.ORDER_SERVICE);
        ProductService productService = (ProductService) getServletContext().getAttribute(AppContextListener.PRODUCT_SERVICE);
        long sellerId = SessionUtil.getUserId(req);

        List<Order> orders = orderService.getOrdersBySeller(sellerId);
        List<OrderViewDTO> views = new ArrayList<>();

        for (Order order : orders) {
            List<OrderItemLineDTO> lines = new ArrayList<>();
            for (OrderItem item : orderService.getOrderItems(order.getId())) {
                Product product = productService.getProductById(item.getProductId());
                // Only show this seller's own items within a (possibly multi-seller) order.
                if (product != null && product.getSellerId() == sellerId) {
                    lines.add(new OrderItemLineDTO(product.getName(), item.getQuantity(), item.getPrice()));
                }
            }
            views.add(new OrderViewDTO(order, lines));
        }

        req.setAttribute("orderViews", views);
        req.getRequestDispatcher("/WEB-INF/views/seller/orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.SELLER)) return;

        OrderService orderService = (OrderService) getServletContext().getAttribute(AppContextListener.ORDER_SERVICE);

        long orderId = Long.parseLong(req.getParameter("orderId"));
        OrderStatus status = OrderStatus.valueOf(req.getParameter("status"));
        orderService.updateStatus(orderId, status);

        resp.sendRedirect(req.getContextPath() + "/seller/orders");
    }
}