package com.wagglex2.waggle.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void sendAuthCode(String toEmail);
    void sendEmailAuthCode(String toEmail, String verificationCode);
    void verifyCode(String toEmail, String inputCode);
    void issueNewTokens(HttpServletRequest request, HttpServletResponse response);
}
