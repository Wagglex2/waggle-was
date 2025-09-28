package com.wagglex2.waggle.domain.common.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseRecruitmentResponseDto {
    private final Long id;
    private final Long authorId;
    private final String authorNickname;
    private final RecruitmentCategory category;
    // TODO profileImg
    // TODO university - merge 후 University enum 반영
    private final String title;
    private final String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime deadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    private final RecruitmentStatus status;
    private int viewCount;

    public void increaseViewCount() {
        this.viewCount++;
    }
}
