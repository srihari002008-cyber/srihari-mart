package com.srihari.sriharimart.listener;

import com.srihari.sriharimart.dao.*;
import com.srihari.sriharimart.dao.impl.*;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.model.User;
import com.srihari.sriharimart.service.*;
import com.srihari.sriharimart.util.DBUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.h2.tools.RunScript;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

public class AppContextListener implements ServletContextListener {

    public static final String DATA_SOURCE = "dataSource";
    public static final String USER_SERVICE = "userService";
    public static final String PRODUCT_SERVICE = "productService";
    public static final String CART_SERVICE = "cartService";
    public static final String ORDER_SERVICE = "orderService";
    public static final String REVIEW_SERVICE = "reviewService";

    private static final String SEED_ADMIN_EMAIL = "admin@srirammart.com";
    private static final String SEED_ADMIN_PASSWORD = "admin123";

    private HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            System.out.println("Starting SriramMart application...");

            dataSource = DBUtil.createDataSource();
            event.getServletContext().setAttribute(DATA_SOURCE, dataSource);

            initializeDatabase();

            UserDAO userDAO = new UserDAOImpl(dataSource);
            ProductDAO productDAO = new ProductDAOImpl(dataSource);
            CartDAO cartDAO = new CartDAOImpl(dataSource);
            OrderDAO orderDAO = new OrderDAOImpl(dataSource);
            ReviewDAO reviewDAO = new ReviewDAOImpl(dataSource);

            UserService userService = new UserService(userDAO);
            ProductService productService = new ProductService(productDAO);
            CartService cartService = new CartService(cartDAO);
            OrderService orderService = new OrderService(orderDAO, productService);
            ReviewService reviewService = new ReviewService(reviewDAO, orderDAO);

            seedAdminIfMissing(userDAO);

            event.getServletContext().setAttribute(USER_SERVICE, userService);
            event.getServletContext().setAttribute(PRODUCT_SERVICE, productService);
            event.getServletContext().setAttribute(CART_SERVICE, cartService);
            event.getServletContext().setAttribute(ORDER_SERVICE, orderService);
            event.getServletContext().setAttribute(REVIEW_SERVICE, reviewService);

            System.out.println("SriramMart initialized successfully.");

        } catch (Exception e) {
            if (dataSource != null) dataSource.close();
            throw new RuntimeException("Failed to initialize SriramMart application", e);
        }
    }

    /** F1: "Admin role assigned via a seed account; no separate admin signup flow." */
    private void seedAdminIfMissing(UserDAO userDAO) {
        if (userDAO.findByEmail(SEED_ADMIN_EMAIL).isPresent()) return;

        String hash = BCrypt.hashpw(SEED_ADMIN_PASSWORD, BCrypt.gensalt());
        User admin = new User(0, "Administrator", SEED_ADMIN_EMAIL, hash, Role.ADMIN);
        userDAO.save(admin);

        System.out.println("Seeded admin account: " + SEED_ADMIN_EMAIL + " / " + SEED_ADMIN_PASSWORD);
    }

    private void initializeDatabase() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("schema.sql");
        if (inputStream == null) {
            throw new IllegalStateException("schema.sql was not found in the application classpath");
        }
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             Connection connection = dataSource.getConnection()) {
            RunScript.execute(connection, reader);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Stopping SriramMart application...");
        if (dataSource != null) dataSource.close();
    }
}