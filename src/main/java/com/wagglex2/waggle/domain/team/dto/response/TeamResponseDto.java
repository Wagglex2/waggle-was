package com.wagglex2.waggle.domain.team.dto.response;

import com.wagglex2.waggle.domain.common.dto.response.PeriodResponseDto;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.team.entity.Team;
import com.wagglex2.waggle.domain.team_member.dto.response.TeamMemberResponseDto;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 팀(Team) 엔티티에 대한 응답 DTO.
 *
 * <p>
 * 필드 설명
 * <ul>
 *   <li><b>id</b>: Team 식별자</li>
 *   <li><b>recruitmentId</b>: 연결된 모집공고(BaseRecruitment) ID</li>
 *   <li><b>recruitmentTitle</b>: 모집공고 제목</li>
 *   <li><b>category</b>: 프로젝트/스터디/과제 구분</li>
 *   <li><b>status</b>: 모집 상태 (RECRUITING, CLOSED, CANCELED 등)</li>
 *   <li><b>period</b>: 모집공고의 기간 정보 (Project/Study에만 존재)</li>
 *   <li><b>durationDays</b>: 기간 일수(끝 - 시작 일자 계산 결과)</li>
 *   <li><b>leaderNickname</b>: 팀 리더(모집공고 작성자)의 닉네임</li>
 *   <li><b>memberCount</b>: 팀 멤버 수</li>
 *   <li><b>members</b>: 각 팀 멤버의 상세 정보 DTO 리스트</li>
 * </ul>
 * </p>
 */
public record TeamResponseDto(
        Long id,
        Long recruitmentId,
        String recruitmentTitle,
        RecruitmentCategory category,
        RecruitmentStatus status,
        PeriodResponseDto period,
        Long durationDays,
        String leaderNickname,
        int memberCount,
        List<TeamMemberResponseDto> members
) {
    public static TeamResponseDto fromEntity(Team team) {
        BaseRecruitment recruitment = team.getRecruitment();
        List<TeamMember> teamMembers = team.getMembers();

        // Project/Study만 기간(Period)을 가지므로 타입 확인 후 변환
        PeriodResponseDto periodResponseDto;
        if (recruitment instanceof Project project) {
            periodResponseDto = PeriodResponseDto.from(project.getPeriod());
        } else if (recruitment instanceof Study study) {
            periodResponseDto = PeriodResponseDto.from(study.getPeriod());
        } else {
            periodResponseDto = null;
        }

        // 기간 일수 계산
        Long durationDays = null;
        if (periodResponseDto != null) {
            long days = ChronoUnit.DAYS.between(
                    periodResponseDto.startDate(),
                    periodResponseDto.endDate()
            );
            durationDays = days >= 0 ? days : null;
        }

        List<TeamMemberResponseDto> memberDtos = teamMembers.stream()
                .map(TeamMemberResponseDto::fromEntity)
                .toList();

        return new TeamResponseDto(
                team.getId(),
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getCategory(),
                recruitment.getStatus(),
                periodResponseDto,
                durationDays,
                recruitment.getUser().getNickname(),
                team.getMembers().size(),
                memberDtos
                );
    }
}
