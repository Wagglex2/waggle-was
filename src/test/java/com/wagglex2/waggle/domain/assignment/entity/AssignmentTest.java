package com.wagglex2.waggle.domain.assignment.entity;

import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentTest {

    @Test
    @DisplayName("Assignment Entity 생성 시 모든 필드가 올바르게 초기화되어야 한다.")
    void createAssignment() {
        // given
        String department = "컴퓨터공학과";
        String lecture = "데이터베이스";
        String lectureCode = "1012";
        ParticipantInfo participants = new ParticipantInfo(5); // 최대 5명, 기본 현재 0명

        // when
        Assignment assignment = Assignment.builder()
                .department(department)
                .lecture(lecture)
                .lectureCode(lectureCode)
                .participants(participants)
                .build();

        // then
        assertThat(assignment.getCategory()).isEqualTo(RecruitmentCategory.ASSIGNMENT);
        assertThat(assignment.getDepartment()).isEqualTo(department);
        assertThat(assignment.getLecture()).isEqualTo(lecture);
        assertThat(assignment.getLectureCode()).isEqualTo(lectureCode);
        assertThat(assignment.getParticipants()).isEqualTo(participants);
        assertThat(assignment.getStatus()).isEqualTo(RecruitmentStatus.RECRUITING);
        assertThat(assignment.getViewCount()).isEqualTo(0);
    }
}