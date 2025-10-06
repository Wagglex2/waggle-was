package com.wagglex2.waggle.domain.study.entity;

import com.wagglex2.waggle.domain.common.type.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StudyTest {

    @Test
    @DisplayName("Study Entity 생성 시 모든 필드가 올바르게 초기화되어야 한다.")
    void createStudy() {
        // given
        String title = "스프링 스터디 모집합니다.";
        String content = "Spring Boot + JPA 같이 공부할 분 구해요!";
        int maxParticipants = 5;

        Set<Skill> skills = Set.of(Skill.SPRING_BOOT, Skill.JAVA);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);
        Period period = new Period(startDate, endDate);
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);

        // when
        Study study = Study.builder()
                .title(title)
                .content(content)
                .deadline(deadline)
                .participants(new ParticipantInfo(maxParticipants))
                .period(period)
                .skills(skills)
                .build();

        // then
        assertThat(study.getCategory()).isEqualTo(RecruitmentCategory.STUDY);
        assertThat(study.getTitle()).isEqualTo(title);
        assertThat(study.getContent()).isEqualTo(content);
        assertThat(study.getParticipants().getMaxParticipants()).isEqualTo(maxParticipants);
        assertThat(study.getSkills()).containsExactlyInAnyOrderElementsOf(skills);
        assertThat(study.getPeriod()).isEqualTo(period);
        assertThat(study.getDeadline()).isEqualTo(deadline);
        assertThat(study.getStatus()).isEqualTo(RecruitmentStatus.RECRUITING);
        assertThat(study.getViewCount()).isEqualTo(0);
    }
}