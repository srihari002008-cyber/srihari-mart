package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.dto.CartLineDTO;
import com.srihari.sriharimart.listener.AppContextListener;
import com.srihari.sriharimart.model.Cart;
import com.srihari.sriharimart.model.CartItem;
import com.srihari.sriharimart.model.Product;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.service.CartService;
import com.srihari.sriharimart.service.ProductService;
import com.srihari.sriharimart.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cart/*")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.BUYER)) return;

        CartService cartService = (CartService) getServletContext().getAttribute(AppContextListener.CART_SERVICE);
        ProductService productService = (ProductService) getServletContext().getAttribute(AppContextListener.PRODUCT_SERVICE);
        long buyerId = SessionUtil.getUserId(req);

        Cart cart = cartService.getCart(buyerId);
        List<CartLineDTO> lines = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            Product product = productService.getProductById(item.getProductId());
            if (product == null) continue; // product deleted after being added to cart

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            lines.add(new CartLineDTO(product, item.getQuantity(), lineTotal));
            grandTotal = grandTotal.add(lineTotal);
        }

        req.setAttribute("lines", lines);
        req.setAttribute("grandTotal", grandTotal);
        req.getRequestDispatcher("/WEB-INF/views/cart/view.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(req, resp, Role.BUYER)) return;

        CartService cartService = (CartService) getServletContext().getAttribute(AppContextListener.CART_SERVICE);
        long buyerId = SessionUtil.getUserId(req);
        String pathInfo = req.getPathInfo();

        try {
            long productId = Long.parseLong(req.getParameter("productId"));

            if ("/add".equals(pathInfo)) {
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                cartService.addItem(buyerId, productId, quantity);

            } else if ("/update".equals(pathInfo)) {
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                cartService.updateItem(buyerId, productId, quantity);

            } else if ("/remove".equals(pathInfo)) {
                cartService.removeItem(buyerId, productId);
            }

        } catch (IllegalArgumentException ignored) {
            // Silently ignore bad input for MVP; redirect shows current cart state either way.
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }
}