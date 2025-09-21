package com.wagglex2.waggle.domain.project.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum ProjectPurpose {
    CONTEST("공모전"),
    HACKATHON("해커톤"),
    TOY_PROJECT("토이 프로젝트"),
    SIDE_PROJECT("사이드 프로젝트");

    private final String desc;

    public String getName() {
        return name();
    }
}
