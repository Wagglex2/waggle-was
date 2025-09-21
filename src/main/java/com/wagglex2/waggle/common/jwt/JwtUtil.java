package com.wagglex2.waggle.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtil {

    private static final String CLAIM_UID = "uid";              // 사용자 ID
    private static final String CLAIM_USERNAME = "username";    // 사용자명
    private static final String CLAIM_NICKNAME = "nickname";    // 닉네임
    private static final String CLAIM_ROLE = "role";            // 권한
    private static final String CLAIM_TYPE = "token_type";      // 토큰 종류
    private static final String TYPE_ACCESS = "access";         // AccessToken
    private static final String TYPE_REFRESH = "refresh";       // RefreshToken

    private final SecretKey secretKey;
    private final long accessExp;
    private final long refreshExp;

    public JwtUtil(@Value("${jwt.secret}") String secretKey,
                   @Value("${jwt.access-exp}") long accessExp,
                   @Value("${jwt.refresh-exp}") long refreshExp) {

        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExp = accessExp;
        this.refreshExp = refreshExp;
    }

    // AccessToken 생성 메서드
    public String createAccessToken(Long userId, String username, String nickname, String role) {
        long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + accessExp);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(String.valueOf(userId))
                .setIssuedAt(iat)
                .setExpiration(exp)
                .claim(CLAIM_UID, userId)
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_NICKNAME, nickname)
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .signWith(secretKey)
                .compact();
    }

    // RefreshToken 생성 메서드
    public String createRefreshToken(Long userId) {
        long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + refreshExp);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(String.valueOf(userId))
                .setIssuedAt(iat)
                .setExpiration(exp)
                .claim(CLAIM_UID, userId)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .signWith(secretKey)
                .compact();
    }

    // JWT Token 파싱 및 검증
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("토큰이 만료되었습니다: {}", e.getMessage());
            throw new JwtException("토큰이 만료되었습니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰 형식입니다: {}", e.getMessage());
            throw new JwtException("지원하지 않는 토큰 형식입니다.", e);
        } catch (MalformedJwtException e) {
            log.error("유효하지 않은 토큰 형식입니다: {}", e.getMessage());
            throw new JwtException("유효하지 않은 토큰 형식입니다.", e);
        } catch (SecurityException | IllegalArgumentException e) {
            log.error("유효하지 않은 토큰 서명입니다: {}", e.getMessage());
            throw new JwtException("유효하지 않은 토큰 서명입니다.", e);
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // 토큰 타입 확인 (AccessToken)
    public boolean isAccessToken(String token) {
        try {
            Claims claims = parseToken(token);
            return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE));
        } catch (JwtException e) {
            return false;
        }
    }

    // 토큰 타입 확인 (RefreshToken)
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            return TYPE_REFRESH.equals(claims.get(CLAIM_TYPE));
        } catch (JwtException e) {
            return false;
        }
    }

    // 토큰에서 사용자 ID 추출
    public Long getUserId(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get(CLAIM_UID, Long.class);
        } catch (JwtException e) {
            log.warn("토큰으로부터 user ID를 불러오는데 실패했습니다: {}", e.getMessage());
            return null;
        }
    }

    // 토큰에서 사용자명(ID) 추출 (AccessToken에만 존재)
    public String getUsername(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get(CLAIM_USERNAME, String.class);
        } catch (JwtException e) {
            log.warn("토큰으로부터 username을 불러오는데 실패했습니다: {}", e.getMessage());
            return null;
        }
    }

    // 토큰에서 닉네임 추출 (AccessToken에만 존재)
    public String getNickname(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get(CLAIM_NICKNAME, String.class);
        } catch (JwtException e) {
            log.warn("토큰으로부터 nickname을 불러오는데 실패했습니다: {}", e.getMessage());
            return null;
        }
    }

    // 토큰 만료 시간 확인
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    // 토큰 만료까지 남은 시간 (ms)
    public long getTimeToExpiration(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (JwtException e) {
            return 0;
        }
    }
}
