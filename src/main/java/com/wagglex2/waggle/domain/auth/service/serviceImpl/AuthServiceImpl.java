package com.wagglex2.waggle.domain.auth.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.domain.auth.dto.request.SignUpRequestDto;
import com.wagglex2.waggle.domain.auth.service.AuthService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

    // Token
    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    // Email
    @Value("${spring.mail.username}")
    private String fromEmail;
    private static final String EMAIL_VERIFICATION_PREFIX = "EMAIL:";
    private static final int EXPIRATION_MINUTES = 3;

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 회원가입 시 이메일 인증을 위해 인증번호를 발급하고, 해당 이메일로 발송한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *     <li>랜덤 6자리 인증번호 생성</li>
     *     <li>Redis에 {@code EMAIL:이메일} 키로 인증번호 저장 (TTL: 3분)</li>
     *     <li>사용자 이메일로 인증번호 발송</li>
     * </ol>
     *
     * @param toEmail 인증번호를 받을 사용자 이메일
     * @throws BusinessException 이메일 발송 실패 시 {@link ErrorCode#EMAIL_SEND_FAILED}
     */
    @Override
    public void sendAuthCode(String toEmail) {

        // 1. 랜덤 6자리 인증번호 생성
        String verificationCode = generateVerificationCode();

        // 2. Redis에 인증번호 등록
        String key = EMAIL_VERIFICATION_PREFIX + toEmail;
        redisTemplate.opsForValue().set(key, verificationCode, Duration.ofMinutes(EXPIRATION_MINUTES));

        // 3. 사용자 이메일로 인증번호 발송
        sendEmailAuthCode(toEmail, verificationCode);
        log.info("인증번호 발송 완료: {}", toEmail);
    }

    /**
     * 실제 이메일 발송 로직을 처리한다.
     *
     * <p>내용:</p>
     * <ul>
     *     <li>보내는 사람: {@code fromEmail} (application-dev.properties에서 설정된 값)</li>
     *     <li>받는 사람: {@code toEmail}</li>
     *     <li>제목: "와글와글 회원가입 이메일 인증"</li>
     *     <li>본문: 인증번호 포함 HTML 템플릿</li>
     * </ul>
     *
     * @param toEmail 인증번호를 받을 이메일
     * @param verificationCode 랜덤으로 생성된 6자리 인증번호
     * @throws BusinessException 이메일 발송 실패 시 {@link ErrorCode#EMAIL_SEND_FAILED}
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
     * 사용자가 입력한 인증번호를 Redis에 저장된 인증번호와 비교하여 검증한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *     <li>이메일 및 입력된 인증번호 null 체크</li>
     *     <li>Redis에서 {@code EMAIL:이메일} 키로 저장된 인증번호 조회</li>
     *     <li>저장된 인증번호가 없으면 만료된 것으로 간주</li>
     *     <li>저장된 인증번호와 입력값 비교 (불일치 시 실패)</li>
     *     <li>검증 성공 시 Redis에서 해당 키 삭제 (재사용 방지)</li>
     * </ol>
     *
     * @param toEmail 인증번호를 받은 이메일
     * @param inputCode 사용자가 입력한 인증번호
     * @throws BusinessException
     *         <ul>
     *             <li>{@link ErrorCode#INVALID_REQUEST} : 이메일 또는 인증번호가 누락됨</li>
     *             <li>{@link ErrorCode#VERIFICATION_CODE_EXPIRED} : 인증번호가 존재하지 않거나 만료됨</li>
     *             <li>{@link ErrorCode#INVALID_VERIFICATION_CODE} : 입력된 인증번호 불일치</li>
     *         </ul>
     */
    @Override
    public void verifyCode(String toEmail, String inputCode) {

        // 1. 이메일 및 입력된 인증번호 null 체크
        if (toEmail.isBlank() || inputCode.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이메일 또는 인증번호가 누락되었습니다.");
        }

        // 2. Redis에서 키로 저장된 인증번호 조회
        String key = EMAIL_VERIFICATION_PREFIX + toEmail;
        String storedCode = redisTemplate.opsForValue().get(key);

        // 3. 저장된 인증번호가 없으면 만료된 것으로 간주
        if (storedCode == null) {
            log.warn("인증번호가 존재하지 않거나 만료되었습니다: {}", toEmail);
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        // 4. 저장된 인증번호와 입력값 비교
        if (!storedCode.equals(inputCode.trim())) {
            log.warn("인증번호가 일치하지 않습니다: {}", toEmail);
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 5. 검증 시 Redis에서 해당 키 삭제
        redisTemplate.delete(key);
    }

    /**
     * Refresh Token을 검증하고 새로운 Access Token과 Refresh Token을 발급한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *     <li>쿠키에서 Refresh Token 추출</li>
     *     <li>토큰 유효성 및 Redis 저장 토큰과의 일치 여부 확인</li>
     *     <li>사용자 존재 여부 확인</li>
     *     <li>Access Token과 Refresh Token 재발급</li>
     *     <li>Redis에 Refresh Token 갱신</li>
     *     <li>쿠키에 두 토큰 설정</li>
     * </ol>
     *
     * @param request  Refresh Token을 담고 있는 HTTP 요청
     * @param response 새로운 토큰을 쿠키로 담아 반환할 HTTP 응답
     * @throws BusinessException REFRESH_TOKEN_INVALID, REFRESH_TOKEN_MISMATCH, USER_NOT_FOUND
     */
    @Override
    public void issueNewTokens(HttpServletRequest request, HttpServletResponse response) {

        // 1. Cookie로부터 Refresh Token 추출
        String refreshToken = extractRefreshToken(request);

        // 2. 토큰 유효성 검사
        if (!StringUtils.hasText(refreshToken) || !jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            log.warn("리프레시 토큰이 유효하지 않습니다.");
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 3. Redis 저장 토큰과 일치 여부 확인
        Long userId = jwtUtil.getUserId(refreshToken);
        String redisKey = REFRESH_TOKEN_PREFIX + userId;
        String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);

        if (!StringUtils.hasText(storedRefreshToken) || !BCrypt.checkpw(refreshToken, storedRefreshToken)) {
            log.warn("리프레시 토큰이 일치하지 않습니다: {}", userId);
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // 4. 사용자 존재 여부 확인
        User user = userService.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 5. 새로운 Access Token 및 Refresh Token 재발급
        String newAccessToken = jwtUtil.createAccessToken(
                userId, user.getUsername(), user.getNickname(), user.getRole().name());
        String newRefreshToken = jwtUtil.createRefreshToken(userId);
        String newHashedRefreshToken = BCrypt.hashpw(newRefreshToken, BCrypt.gensalt());

        // 6. Redis 새로운 Refresh Token 갱신
        redisTemplate.opsForValue().set(redisKey, newHashedRefreshToken, jwtUtil.getRefreshExpMills(), TimeUnit.MILLISECONDS);

        // 7. 쿠키에 두 토큰 설정
        setTokenCookie(response, newAccessToken, ACCESS_TOKEN_COOKIE_NAME, jwtUtil.getAccessExpMills() / 1000);
        setTokenCookie(response, newRefreshToken, REFRESH_TOKEN_COOKIE_NAME, jwtUtil.getRefreshExpMills() / 1000);
    }

    /**
     * 6자리 숫자 형식의 랜덤 인증번호를 생성한다.
     *
     * @return "000000" ~ "999999" 범위의 문자열 인증번호
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%06d", random.nextInt(1_000_000));
    }

    /**
     * 이메일 본문으로 사용될 HTML 템플릿을 생성한다.
     *
     * <p>구성:</p>
     * <ul>
     *     <li>제목: "회원가입 이메일 인증"</li>
     *     <li>본문: 인증번호를 크게 표시</li>
     *     <li>주의 문구: "3분 이내에 입력해주세요"</li>
     *     <li>하단 안내: 요청하지 않았다면 무시</li>
     * </ul>
     *
     * @param verificationCode 이메일에 삽입할 6자리 인증번호
     * @return HTML 문자열
     */
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

    /**
     * HTTP 요청의 쿠키에서 Refresh Token 값을 추출한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *     <li>요청 객체에 쿠키가 있는지 확인</li>
     *     <li>쿠키 목록에서 이름이 {@code REFRESH_TOKEN_COOKIE_NAME}과 일치하는 쿠키 검색</li>
     *     <li>해당 쿠키의 값을 반환, 없으면 {@code null} 반환</li>
     * </ol>
     *
     * @param request Refresh Token을 담고 있을 수 있는 HTTP 요청
     * @return Refresh Token 문자열, 존재하지 않으면 {@code null}
     */
    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * 주어진 토큰을 응답 쿠키로 설정한다.
     *
     * <p>설정 속성:</p>
     * <ul>
     *     <li>{@code HttpOnly}: true (JS 접근 불가, 보안 강화)</li>
     *     <li>{@code Secure}: true (HTTPS 연결에서만 전송)</li>
     *     <li>{@code SameSite}: Lax (기본 CSRF 방어)</li>
     *     <li>{@code Path}: "/" (애플리케이션 전체 경로에서 사용 가능)</li>
     *     <li>{@code Max-Age}: 토큰 만료 시간(초 단위)</li>
     * </ul>
     *
     * @param response   쿠키를 추가할 HTTP 응답
     * @param token      쿠키에 저장할 토큰 (Access 또는 Refresh Token)
     * @param cookieName 쿠키 이름
     * @param maxAge     쿠키 만료 시간 (초 단위)
     */
    private void setTokenCookie(HttpServletResponse response, String token, String cookieName, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
