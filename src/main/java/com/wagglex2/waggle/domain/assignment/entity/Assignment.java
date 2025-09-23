package com.wagglex2.waggle.domain.assignment.entity;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 과제 모집 공고 엔티티.
 * <p>
 * BaseRecruitment를 상속하여 유저, 제목, 본문, 마감일 등)을 포함하며
 * 과제 공고에 필요한 추가 필드를 정의한다.
 * </p>
 *
 * <ul>
 *   <li>department : 개설 학과명</li>
 *   <li>lecture : 과목명</li>
 *   <li>lectureCode : 과목 코드</li>
 * </ul>
 *
 * @author 박대형
 * @see BaseRecruitment
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Assignment extends BaseRecruitment {
    private String department;
    private String lecture;
    private String lectureCode;

    public Assignment(
            String department,
            String lecture,
            String lectureCode
    ) {
        this.department = department;
        this.lecture = lecture;
        this.lectureCode = lectureCode;
    }
}
