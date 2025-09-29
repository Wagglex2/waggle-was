package com.wagglex2.waggle.domain.user.entity.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum University {
    YOUNGNAM_UNIV("영남대", "yu.ac.kr"),
    KYUNGBUK_UNIV("경북대", "knu.ac.kr"),
    KUMOH_UNIV("금오공대", "kumoh.ac.kr"),
    GYEONGGUK_NATIONAL_UNIV("국립경국대", "gknu.ac.kr"),
    POSTECH("포항공대", "postech.ac.kr"),
    DAEGU_UNIV("대구대", "deagu.ac.kr"),
    KEIMYUNG_UNIV("계명대", "stu.kmu.ac.kr");

    private final String desc;

    private final String domain;

    public  String getName() {
        return this.name();
    }

    public static University fromEmail(String email) {
        String domainPart = email.substring(email.indexOf("@") + 1);

        for (University university : values()) {
            if (university.getDomain().equalsIgnoreCase(domainPart)) {
                return university;
            }
        }

        throw new BusinessException(ErrorCode.UNSUPPORTED_UNIVERSITY_DOMAIN);
    }
}
