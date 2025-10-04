package com.wagglex2.waggle.domain.user.dto.request;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


/**
 * 비밀번호 변경 요청 DTO.
 *
 * <p>요청 필드:</p>
 * <ul>
 *   <li>old: 기존 비밀번호 (NotBlank, 최대 72자)</li>
 *   <li>newPassword: 새로운 비밀번호 (NotBlank, 8~72자, 영문/숫자/특수문자 포함)</li>
 *   <li>passwordConfirm: 새로운 비밀번호 확인 (NotBlank)</li>
 * </ul>
 *
 * <p>검증 로직:</p>
 * <ul>
 *   <li>기존 비밀번호와 새로운 비밀번호가 동일하면 예외 발생 (PASSWORD_SAME_AS_OLD)</li>
 *   <li>새로운 비밀번호와 확인 비밀번호가 일치하지 않으면 예외 발생 (MISMATCHED_PASSWORD)</li>
 * </ul>
 *
 * @throws BusinessException PASSWORD_SAME_AS_OLD, MISMATCHED_PASSWORD
 */
public record PasswordRequestDto(
        @NotBlank(message = "기존 비밀번호가 누락되었습니다.")
        @Size(max = 72, message = "비밀번호는 72자 이하입니다.")
        String old,

        @NotBlank(message = "새로운 비밀번호가 누락되었습니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+~])[A-Za-z\\d!@#$%^&*()_+~]{8,72}$",
                message = "비밀번호는 8자 이상 72자 이내의 영문, 숫자, 특수문자를 포함해야 합니다.")
        String newPassword,

        @NotBlank(message = "비밀번호 확인이 누락되었습니다.")
        String passwordConfirm
) {
    public PasswordRequestDto {
        if (old != null && old.equals(newPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }

        if (newPassword != null && passwordConfirm != null && !newPassword.equals(passwordConfirm)) {
            throw new BusinessException(ErrorCode.MISMATCHED_PASSWORD);
        }
    }
}
