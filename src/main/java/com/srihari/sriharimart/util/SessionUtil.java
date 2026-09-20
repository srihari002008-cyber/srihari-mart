package com.srihari.sriharimart.util;

import com.srihari.sriharimart.model.Role;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public final class SessionUtil {

    private SessionUtil() {}

    public static Long getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (Long) session.getAttribute("userId");
    }

    public static String getUserRole(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (String) session.getAttribute("userRole");
    }

    /** Returns true if the session has one of the allowed roles; otherwise sends redirect/403 and returns false. */
    public static boolean requireRole(HttpServletRequest req, HttpServletResponse resp, Role... allowed) throws IOException {
        String role = getUserRole(req);

        if (role == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }

        for (Role r : allowed) {
            if (r.name().equals(role)) return true;
        }

        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this resource");
        return false;
    }
}