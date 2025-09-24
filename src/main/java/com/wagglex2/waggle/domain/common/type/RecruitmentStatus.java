package com.wagglex2.waggle.domain.common.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum RecruitmentStatus {
    RECRUITING("모집 중"),
    CLOSED("마감"),
    CANCELED("취소");

    private final String desc;

    public String getName() {
        return this.name();
    }
}

