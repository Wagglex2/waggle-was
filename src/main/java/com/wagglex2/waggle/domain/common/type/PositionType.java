package com.wagglex2.waggle.domain.common.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum PositionType {
    FULL_STACK("풀스택"),
    FRONT_END("프론트엔드"),
    BACK_END("백엔드"),
    DATA("데이터"),
    AI("AI"),
    GAME("게임"),
    PLANNER("기획"),
    DESIGNER("디자인");

    private final String desc;

    public String getName() {
        return this.name();
    }
}
