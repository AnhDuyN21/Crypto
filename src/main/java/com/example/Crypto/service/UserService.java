package com.example.Crypto.service;

import com.example.Crypto.entity.User;

import java.util.Optional;

public interface UserService {
    User register(User user);
    Optional<User> findByUsername(String username);
}

