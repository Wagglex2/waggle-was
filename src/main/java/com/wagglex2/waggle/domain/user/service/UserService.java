package com.wagglex2.waggle.domain.user.service;

import com.wagglex2.waggle.domain.user.entity.User;

public interface UserService {
    User findByUsername(String username);
    User findById(Long id);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
