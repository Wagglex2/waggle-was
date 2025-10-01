package com.wagglex2.waggle.domain.user.service;

import com.wagglex2.waggle.domain.auth.dto.request.SignUpRequestDto;
import com.wagglex2.waggle.domain.user.dto.request.PasswordRequestDto;
import com.wagglex2.waggle.domain.user.dto.response.UserResponseDto;
import com.wagglex2.waggle.domain.user.entity.User;

public interface UserService {
    User findByUsername(String username);
    User findById(Long id);
    User findByIdWithSkills(Long id);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    Long signUp(SignUpRequestDto dto);
    void changePassword(Long userId, PasswordRequestDto dto);
    UserResponseDto getUserInfo(Long userId);
}
