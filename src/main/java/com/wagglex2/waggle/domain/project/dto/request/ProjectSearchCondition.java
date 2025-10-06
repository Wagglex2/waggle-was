package com.wagglex2.waggle.domain.project.dto.request;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;

import java.util.Set;

public record ProjectSearchCondition(
        String keywords,
        ProjectPurpose purpose,
        Set<PositionType> positions,
        Set<Skill> skills,
        RecruitmentStatus status
) {
    public ProjectSearchCondition {
        // Collection의 불변성 보장
        positions = (positions == null) ? Set.of() : Set.copyOf(positions);
        skills = (skills == null) ? Set.of() : Set.copyOf(skills);
    }
}
