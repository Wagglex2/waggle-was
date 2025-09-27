package com.wagglex2.waggle.domain.auth.dto.request;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

public record SignUpRequestDto(

        @NotBlank(message = "아이디가 누락되었습니다.")
        @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "아이디는 4-20자의 영문, 숫자, 언더스코어만 가능합니다.")
        String username,

        @NotBlank(message = "비밀번호가 누락되었습니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+~])[A-Za-z\\d!@#$%^&*()_+~]{8,}$",
                message = "비밀번호는 8자 이상의 영문, 숫자, 특수문자를 포함해야 합니다.")
        String password,

        @NotBlank(message = "비밀번호 확인이 누락되었습니다.")
        String passwordConfirm,

        @NotBlank(message = "닉네임이 누락되었습니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
                message = "닉네임은 2-10자의 영문, 한글, 숫자만 입력할 수 있습니다.")
        String nickname,

        @NotBlank(message = "이메일이 누락되었습니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @NotNull(message = "학년이 누락되었습니다.")
        @Min(value = 1, message = "학년은 1학년 이상이어야 합니다.")
        @Max(value = 4, message = "학년은 4학년 이하여야 합니다.")
        Integer grade,

        @NotNull(message = "포지션이 누락되었습니다.")
        PositionType position,

        @NotEmpty(message = "기술 스택이 누락되었습니다.")
        Set<Skill> skills,

        @NotBlank(message = "한 줄 소개가 누락되었습니다.")
        @Size(max = 255, message = "한 줄 소개는 100자를 초과할 수 없습니다.")
        String shortIntro
) {
    public SignUpRequestDto {
        if (password != null && passwordConfirm != null && !password.equals(passwordConfirm)) {
            throw new BusinessException(ErrorCode.MISMATCHED_PASSWORD);
        }
    }

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .email(email)
                .grade(grade)
                .position(position)
                .skills(skills)
                .shortIntro(shortIntro)
                .build();
    }
}

