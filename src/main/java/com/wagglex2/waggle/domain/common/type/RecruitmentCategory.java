package com.wagglex2.waggle.domain.common.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RecruitmentCategory {
    PROJECT("프로젝트"),
    ASSIGNMENT("과제"),
    STUDY("스터디");

    private final String desc;

    public String getName() {
        return this.name();
    }
}