package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.dto.OrderItemLineDTO;
import com.srihari.sriharimart.dto.OrderViewDTO;
import com.srihari.sriharimart.listener.AppContextListener;
import com.srihari.sriharimart.model.Order;
import com.srihari.sriharimart.model.OrderItem;
import com.srihari.sriharimart.model.Product;
import com.srihari.sriharimart.model.Role;
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

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.BUYER)) return;

        OrderService orderService = (OrderService) getServletContext().getAttribute(AppContextListener.ORDER_SERVICE);
        ProductService productService = (ProductService) getServletContext().getAttribute(AppContextListener.PRODUCT_SERVICE);
        long buyerId = SessionUtil.getUserId(req);

        List<Order> orders = orderService.getOrdersByBuyer(buyerId);
        List<OrderViewDTO> views = new ArrayList<>();

        for (Order order : orders) {
            List<OrderItemLineDTO> lines = new ArrayList<>();
            for (OrderItem item : orderService.getOrderItems(order.getId())) {
                Product product = productService.getProductById(item.getProductId());
                String name = product != null ? product.getName() : "(deleted product)";
                lines.add(new OrderItemLineDTO(name, item.getQuantity(), item.getPrice()));
            }
            views.add(new OrderViewDTO(order, lines));
        }

        req.setAttribute("orderViews", views);
        req.getRequestDispatcher("/WEB-INF/views/orders/my.jsp").forward(req, resp);
    }
}