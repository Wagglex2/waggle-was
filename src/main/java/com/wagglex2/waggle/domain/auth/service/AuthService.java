package com.wagglex2.waggle.domain.auth.service;

public interface AuthService {
    public void sendAuthCode(String toEmail);
    public void sendEmailAuthCode(String toEmail, String verificationCode);
    public void verifyCode(String toEmail, String inputCode);

}
