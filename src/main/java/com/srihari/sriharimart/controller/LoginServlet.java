package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.listener.AppContextListener;
import com.srihari.sriharimart.model.User;
import com.srihari.sriharimart.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserService userService = (UserService) getServletContext()
                .getAttribute(AppContextListener.USER_SERVICE);

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        User user = userService.login(email, password);

        if (user == null) {
            req.setAttribute("error", "Invalid email or password");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        // Create session AFTER verifying credentials, then regenerate the session ID
        // (spec Section 2 Rule 3: prevents session fixation attacks)
        HttpSession session = req.getSession(true);
        req.changeSessionId();

        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userRole", user.getRole().name());

        resp.sendRedirect(req.getContextPath() + "/");
    }
}