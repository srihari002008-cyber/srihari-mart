package com.srihari.sriharimart.dao;

import com.srihari.sriharimart.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    void save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(long id);
    List<User> findAll();
}