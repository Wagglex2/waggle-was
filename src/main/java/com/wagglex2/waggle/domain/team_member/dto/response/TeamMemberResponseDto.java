package com.wagglex2.waggle.domain.team_member.dto.response;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;
import com.wagglex2.waggle.domain.team_member.entity.type.TeamRole;

/**
 * 팀 멤버(TeamMember) 정보를 표현하는 응답 DTO.
 *
 * <p>
 *  - 필드 설명
 * <ul>
 *   <li><b>userId</b> : 팀 멤버(User)의 고유 ID</li>
 *   <li><b>nickname</b> : 사용자 닉네임</li>
 *   <li><b>role</b> : 팀 내 역할 (LEADER, MEMBER 등, {@link TeamRole})</li>
 *   <li><b>position</b> : 사용자의 포지션 (예: BACKEND, FRONTEND 등, {@link PositionType})</li>
 *   <li><b>// TODO ProfileImage</b> : 추후 프로필 이미지 URL 추가 예정</li>
 * </ul>
 * </p>
 */
public record TeamMemberResponseDto(
        Long userId,
        // TODO ProfileImage
        String nickname,
        TeamRole role,
        PositionType position
) {
    public static TeamMemberResponseDto fromEntity(TeamMember teamMember) {
        return new TeamMemberResponseDto(
                teamMember.getUser().getId(),
                teamMember.getUser().getNickname(),
                teamMember.getRole(),
                teamMember.getPosition()
        );
    }
}
