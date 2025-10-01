package com.wagglex2.waggle.domain.user.dto.response;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.user.entity.User;

import java.util.Set;

/**
 * UserResponseDto
 *
 * <p>사용자 정보를 외부 API 응답으로 전달하기 위한 DTO.
 * User 엔티티를 직접 노출하지 않고, 필요한 속성만 추려서 제공한다.</p>
 *
 */
public record UserResponseDto(
        String username,
        String email,
        String nickname,
        Integer grade,
        PositionType position,
        Set<Skill> skills,
        String shortIntro
) {
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getGrade(),
                user.getPosition(),
                user.getSkills(),
                user.getShortIntro()
        );
    }
}
