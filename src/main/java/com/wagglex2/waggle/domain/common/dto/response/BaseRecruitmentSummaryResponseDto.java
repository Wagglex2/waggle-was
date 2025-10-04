package com.wagglex2.waggle.domain.common.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.user.entity.type.University;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseRecruitmentSummaryResponseDto {
    private final Long id;
    private final Long authorId;
    private final String authorNickname;
    private final RecruitmentCategory category;
    // TODO profileImg
    private final University university;
    private final String title;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDateTime deadline;

    private final RecruitmentStatus status;
}
