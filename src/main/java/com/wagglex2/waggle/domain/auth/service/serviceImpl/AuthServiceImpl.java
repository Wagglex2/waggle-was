package com.wagglex2.waggle.domain.auth.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.auth.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String EMAIL_VERIFICATION_PREFIX = "EMAIL:";
    private static final int EXPIRATION_MINUTES = 3;

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis에 이메일과 인증번호를 저장한다.
     * TTL(Time To Live): 3분
     * @param toEmail 인증번호를 받을 이메일
     */
    @Override
    @Transactional
    public void sendAuthCode(String toEmail) {
        String verificationCode = generateVerificationCode();

        // Redis에 Email과 인증번호 등록
        String key = EMAIL_VERIFICATION_PREFIX + toEmail;
        redisTemplate.opsForValue().set(key, verificationCode, Duration.ofMinutes(EXPIRATION_MINUTES));

        sendEmailAuthCode(toEmail, verificationCode);
        log.info("인증번호 발송 완료: {}", toEmail);
    }

    /**
     * 인증번호 확인 메일을 보낸다.
     * @param toEmail 인증번호를 받을 이메일
     * @param verificationCode 랜덤으로 생성한 인증번호
     */
    @Override
    public void sendEmailAuthCode(String toEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("와글와글 회원가입 이메일 인증");

            String htmlContent = createEmailTemplate(verificationCode);

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("이메일 발송 성공: {}", toEmail);

        } catch (MessagingException e) {
            log.error("이메일 발송 실패: {}", toEmail, e);
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 유저가 적은 인증번호와 랜덤으로 생성한 인증번호가 일치하는지 확인한다.
     * @param toEmail 인증번호를 받은 이메일
     * @param inputCode 유저가 적은 인증번호
     */
    @Override
    @Transactional
    public void verifyCode(String toEmail, String inputCode) {
        if (toEmail == null || inputCode == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이메일 또는 인증번호가 누락되었습니다.");
        }
        String key = EMAIL_VERIFICATION_PREFIX + toEmail;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            log.warn("인증번호가 존재하지 않거나 만료되었습니다: {}", toEmail);
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        if (!storedCode.equals(inputCode.trim())) {
            log.warn("인증번호가 일치하지 않습니다: {}", toEmail);
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        redisTemplate.delete(key);
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    private String createEmailTemplate(String verificationCode) {
        return String.format(
                "<html><body style='font-family: Arial, sans-serif;'>" +
                        "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                        "<h2 style='color: #333; text-align: center;'>회원가입 이메일 인증</h2>" +
                        "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px; text-align: center;'>" +
                        "<p style='font-size: 16px; margin-bottom: 20px;'>아래 인증번호를 입력해주세요:</p>" +
                        "<div style='font-size: 32px; font-weight: bold; color: #007bff; letter-spacing: 5px; margin: 20px 0;'>%s</div>" +
                        "<p style='color: #dc3545; font-weight: bold;'>3분 이내에 입력해주세요!</p>" +
                        "</div>" +
                        "<p style='font-size: 14px; color: #666; text-align: center; margin-top: 20px;'>" +
                        "본인이 요청하지 않았다면 이 이메일을 무시해주세요." +
                        "</p>" +
                        "</div>" +
                        "</body></html>",
                verificationCode
        );
    }
}
