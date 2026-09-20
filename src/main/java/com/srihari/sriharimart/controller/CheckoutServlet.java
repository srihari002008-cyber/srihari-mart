package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.listener.AppContextListener;
import com.srihari.sriharimart.model.Cart;
import com.srihari.sriharimart.model.Order;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.service.CartService;
import com.srihari.sriharimart.service.OrderService;
import com.srihari.sriharimart.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.BUYER)) return;

        CartService cartService = (CartService) getServletContext().getAttribute(AppContextListener.CART_SERVICE);
        long buyerId = SessionUtil.getUserId(req);
        Cart cart = cartService.getCart(buyerId);

        if (cart.getItems().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }

        req.setAttribute("cart", cart);
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.BUYER)) return;

        CartService cartService = (CartService) getServletContext().getAttribute(AppContextListener.CART_SERVICE);
        OrderService orderService = (OrderService) getServletContext().getAttribute(AppContextListener.ORDER_SERVICE);
        long buyerId = SessionUtil.getUserId(req);

        try {
            Cart cart = cartService.getCart(buyerId);
            Order order = orderService.placeOrder(buyerId, cart);
            cartService.clearCart(buyerId); // cart.getItems() was cleared in-memory; DB rows need explicit clear too
            resp.sendRedirect(req.getContextPath() + "/orders?placed=" + order.getId());

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
        }
    }
}