package com.wagglex2.waggle.domain.user.dto.request;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Range;

import java.util.Set;

/**
 * 사용자 프로필 수정 요청 DTO.
 *
 * <p>PATCH /me 요청 시 클라이언트가 전달하는 데이터 구조를 정의한다.</p>
 * <ul>
 *   <li>모든 필드는 선택적으로 입력할 수 있으며(null 허용), 값이 존재할 경우에만 수정된다.</li>
 *   <li>닉네임, 학년, 포지션, 보유 기술 스택, 한 줄 소개 등을 수정 가능하다.</li>
 *   <li>Bean Validation 애노테이션을 통해 클라이언트 입력에 대한 유효성 검증을 수행한다.</li>
 * </ul>
 */
public record UserUpdateRequestDto(
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
                message = "닉네임은 2-10자의 영문, 한글, 숫자만 입력할 수 있습니다.")
        String nickname,

        @Range(min = 1, max = 4, message = "모집 학년은 1 이상 4 이하여야 합니다.")
        Integer grade,

        PositionType position,

        Set<Skill> skills,

        @Size(max = 255, message = "한 줄 소개는 100자를 초과할 수 없습니다.")
        String shortIntro
) {
}
