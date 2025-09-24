package com.wagglex2.waggle.domain.project.repository;

import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProjectRepositoryTest {
    @Autowired
    ProjectRepository projectRepository;

    @Test
    @DisplayName("Project 등록 시 모든 필드가 올바르게 저장되어야 한다.")
    void saveProject() {
        // given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        Period period = new Period(startDate, endDate);
        LocalDateTime deadline = LocalDateTime.now().minusDays(3);

        Project project = Project.builder()
                .title("해커톤 팀원 구합니다.")
                .content("카카오에서 개최하는 해커톤 같이 할 사람 구해요.")
                .purpose(ProjectPurpose.HACKATHON)
                .meetingType(MeetingType.HYBRID)
                .positions(Arrays.asList(
                        new PositionParticipantInfo(PositionType.FRONT_END, new ParticipantInfo(3)),
                        new PositionParticipantInfo(PositionType.BACK_END, new ParticipantInfo(3))
                ))
                .skills(Arrays.asList(Skill.REACT, Skill.SPRING_BOOT))
                .grades(Arrays.asList(3, 4))
                .period(period)
                .deadline(deadline)
                .build();

        // when
        Project saved = projectRepository.save(project);

        // then
        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getTitle()).isEqualTo(project.getTitle());
        assertThat(saved.getContent()).isEqualTo(project.getContent());
        assertThat(saved.getPurpose()).isEqualTo(project.getPurpose());
        assertThat(saved.getMeetingType()).isEqualTo(project.getMeetingType());
        assertThat(saved.getPositions()).containsExactlyInAnyOrderElementsOf(project.getPositions());
        assertThat(saved.getSkills()).containsExactlyInAnyOrderElementsOf(project.getSkills());
        assertThat(saved.getGrades()).containsExactlyInAnyOrderElementsOf(project.getGrades());
        assertThat(saved.getPeriod()).isEqualTo(project.getPeriod());
        assertThat(saved.getDeadline()).isEqualTo(project.getDeadline());
    }
}
