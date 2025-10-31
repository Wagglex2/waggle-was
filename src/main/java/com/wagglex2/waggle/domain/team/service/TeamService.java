package com.wagglex2.waggle.domain.team.service;

import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.team.dto.response.TeamResponseDto;
import com.wagglex2.waggle.domain.team.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamService {
    void save(Team team);
    Page<TeamResponseDto> getByUserIdAndCategoryAndStatus(Long userId, RecruitmentCategory category, RecruitmentStatus status, Pageable pageable);
}
