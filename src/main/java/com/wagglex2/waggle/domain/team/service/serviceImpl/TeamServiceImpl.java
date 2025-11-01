package com.wagglex2.waggle.domain.team.service.serviceImpl;

import com.wagglex2.waggle.common.validator.PageableValidator;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.team.dto.response.TeamResponseDto;
import com.wagglex2.waggle.domain.team.entity.Team;
import com.wagglex2.waggle.domain.team.repository.TeamRepository;
import com.wagglex2.waggle.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final PageableValidator pageableValidator;

    private static final Set<String> MY_TEAM_SORT_FIELDS =
            Set.of("createdAt", "updatedAt", "id");

    @Override
    @Transactional
    public void save(Team team) {
        teamRepository.save(team);
    }

    /**
     * 특정 사용자가 생성한 모집공고(프로젝트/스터디 등)에 속한 팀 목록을
     * 카테고리(category)와 상태(status)에 따라 페이징 조회하는 서비스 메서드.
     *
     * @param userId   모집공고 작성자(User)의 ID
     * @param category 모집 카테고리 (PROJECT, STUDY, ASSIGNMENT 등)
     * @param status   모집 상태 (RECRUITING, CLOSED, CANCELED 등)
     * @param pageable 페이지 및 정렬 정보
     * @return         Page 형태의 TeamResponseDto 목록
     */
    @Override
    public Page<TeamResponseDto> getByUserIdAndCategoryAndStatus(
            Long userId,
            RecruitmentCategory category,
            RecruitmentStatus status,
            Pageable pageable
    ) {

        pageableValidator.validate(pageable);
        pageableValidator.validateSort(pageable, MY_TEAM_SORT_FIELDS);

        Page<Team> teams = teamRepository.findDistinctByRecruitmentCategoryAndRecruitmentStatusAndRecruitmentUserId(
                category, status, userId, pageable
        );

        teams.getContent().forEach(team -> {
            team.getMembers().size(); // BatchSize 트리거
        });

        return teams.map(TeamResponseDto::fromEntity);
    }
}
