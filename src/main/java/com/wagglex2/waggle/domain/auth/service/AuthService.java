package com.wagglex2.waggle.domain.auth.service;

import com.wagglex2.waggle.domain.auth.dto.request.SignInRequestDto;
import com.wagglex2.waggle.domain.auth.dto.response.TokenPair;

public interface AuthService {
    void sendAuthCode(String toEmail);
    void sendEmailAuthCode(String toEmail, String verificationCode);
    void verifyCode(String toEmail, String inputCode);
    TokenPair login(SignInRequestDto dto);
    TokenPair reissueTokens(String refreshToken);
}
