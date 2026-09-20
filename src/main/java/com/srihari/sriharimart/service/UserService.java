package com.srihari.sriharimart.service;

import com.srihari.sriharimart.dao.UserDAO;
import com.srihari.sriharimart.model.Role;
import comsrihari.sriharimart.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User registerBuyer(String name, String email, String password) {
        return createUser(name, email, password, Role.BUYER);
    }

    public User registerSeller(String name, String email, String password) {
        return createUser(name, email, password, Role.SELLER);
    }

    /** Returns the user if the password matches, otherwise null. */
    public User login(String email, String password) {
        Optional<User> found = userDAO.findByEmail(email);

        if (found.isEmpty()) return null;

        User user = found.get();
        return BCrypt.checkpw(password, user.getPasswordHash()) ? user : null;
    }

    private User createUser(String name, String email, String password, Role role) {
        validateUserDetails(name, email, password);

        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(0, name, email, hash, role);

        userDAO.save(user);
        return user;
    }

    private void validateUserDetails(String name, String email, String password) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be empty");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password cannot be empty");
    }
}