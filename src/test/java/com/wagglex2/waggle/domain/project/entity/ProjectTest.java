package com.wagglex2.waggle.domain.project.entity;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.Position;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProjectTest {

    @Test
    @DisplayName("Project Entity 생성 시 모든 필드가 올바르게 초기화되어야 한다.")
    void createProject() {
        // given
        String title = "해커톤 팀원 구합니다.";
        String content = "카카오에서 개최하는 해커톤 같이 할 사람 구해요.";
        ProjectPurpose purpose = ProjectPurpose.HACKATHON;
        MeetingType meetingType = MeetingType.HYBRID;

        List<Position> positions = new ArrayList<>();
        positions.add(new Position(PositionType.FRONT_END, 2));
        positions.add(new Position(PositionType.BACK_END, 3));

        List<Skill> skills = new ArrayList<>();
        skills.add(Skill.REACT);
        skills.add(Skill.SPRING_BOOT);

        List<Integer> grades = Arrays.asList(3, 4);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(10);
        LocalDateTime deadline = LocalDateTime.now().minusDays(3);

        // when
        Project project = Project.builder()
                .title(title)
                .content(content)
                .purpose(purpose)
                .meetingType(meetingType)
                .positions(positions)
                .skills(skills)
                .grades(grades)
                .startDate(startDate)
                .endDate(endDate)
                .deadline(deadline)
                .build();

        // then
        assertThat(project.getTitle()).isEqualTo(title);
        assertThat(project.getContent()).isEqualTo(content);
        assertThat(project.getPurpose()).isEqualTo(purpose);
        assertThat(project.getMeetingType()).isEqualTo(meetingType);
        assertThat(project.getPositions()).containsExactlyInAnyOrderElementsOf(positions);
        assertThat(project.getSkills()).containsExactlyInAnyOrderElementsOf(skills);
        assertThat(project.getGrades()).containsExactlyInAnyOrderElementsOf(grades);
        assertThat(project.getStartDate()).isEqualTo(startDate);
        assertThat(project.getEndDate()).isEqualTo(endDate);
        assertThat(project.getDeadline()).isEqualTo(deadline);
        assertThat(project.getStatus()).isEqualTo(RecruitmentStatus.RECRUITING);
        assertThat(project.getViewCount()).isEqualTo(0);
    }
}
