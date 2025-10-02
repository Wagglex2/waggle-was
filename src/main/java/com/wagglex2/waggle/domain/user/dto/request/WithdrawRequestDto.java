package com.wagglex2.waggle.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원 탈퇴 요청 DTO
 *
 * <p>회원 탈퇴 시 본인 확인을 위해 비밀번호를 입력받는 요청 객체</p>
 *
 * <p><b>검증 조건:</b></p>
 * <ul>
 *   <li>{@code @NotBlank} : 비밀번호가 null, 빈 문자열, 공백만으로 입력되는 경우 허용하지 않음</li>
 *   <li>{@code @Size(max = 72)} : BCrypt 해시 알고리즘의 제한에 맞춰 최대 72자까지만 허용</li>
 * </ul>
 *
 * <p><b>사용처:</b></p>
 * <ul>
 *   <li>회원 탈퇴 API 요청 본문</li>
 *   <li>비밀번호 재확인을 통해 세션 탈취 등 보안 사고 방지</li>
 * </ul>
 */
public record WithdrawRequestDto(
        @Size(max = 72, message = "비밀번호는 72자 이내입니다.")
        @NotBlank(message = "비밀번호가 누락되었습니다.")
        String password
) {
}
