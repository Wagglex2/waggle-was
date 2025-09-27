package com.wagglex2.waggle.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "요청 값이 유효하지 않습니다."),
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "REQUIRED_FIELD_MISSING", "필수 값이 누락되었습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_EMAIL_FORMAT", "이메일 형식이 올바르지 않습니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_NICKNAME_FORMAT", "닉네임 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD_FORMAT", "비밀번호 형식이 올바르지 않습니다."),
    MISMATCHED_PASSWORD(HttpStatus.BAD_REQUEST, "MISMATCHED_PASSWORD", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "DUPLICATED_EMAIL", "이미 가입된 이메일입니다."),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "DUPLICATED_NICKNAME", "이미 존재하는 닉네임입니다."),
    UNSUPPORTED_UNIVERSITY_DOMAIN(HttpStatus.BAD_REQUEST, "UNSUPPORTED_UNIVERSITY_DOMAIN", "지원하지 않는 학교 도메인입니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "VERIFICATION_CODE_EXPIRED", "인증번호가 만료되었습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "INVALID_VERIFICATION_CODE", "인증번호가 일치하지 않습니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_INVALID", "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_MISMATCH", "리프레시 토큰이 일치하지 않습니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "ACCESS_TOKEN_INVALID", "유효하지 않은 액세스 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_NOT_FOUND", "리프레시 토큰을 찾을 수 없습니다."),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "요청한 리소스에 접근할 수 없습니다."),

    // 404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE_NOT_FOUND", "파일을 찾을 수 없습니다."),
    POSITION_NOT_FOUND(HttpStatus.NOT_FOUND, "POSITION_NOT_FOUND", "역할을 찾을 수 없습니다."),
    SKILL_NOT_FOUND(HttpStatus.NOT_FOUND, "SKILL_NOT_FOUND", "기술 스택을 찾을 수 없습니다."),

    // 406
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "NOT_ACCEPTABLE", "응답 가능한 미디어 타입이 없습니다."),

    // 413
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", "요청 또는 파일 크기가 너무 큽니다."),

    // 415
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "지원하지 않는 Content-Type 입니다."),

    // 429
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "데이터베이스 오류가 발생했습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_FAILED", "이메일 발송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
