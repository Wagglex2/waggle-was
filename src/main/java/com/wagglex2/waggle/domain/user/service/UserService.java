package com.wagglex2.waggle.domain.user.service;

import com.wagglex2.waggle.domain.user.entity.User;

public interface UserService {
    User findByUsername(String username);
}
