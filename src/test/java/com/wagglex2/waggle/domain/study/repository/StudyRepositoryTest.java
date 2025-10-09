package com.wagglex2.waggle.domain.study.repository;

import com.wagglex2.waggle.common.config.JpaAuditingConfig;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import com.wagglex2.waggle.domain.user.entity.type.UserRoleType;
import com.wagglex2.waggle.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingConfig.class)
@DataJpaTest
class StudyRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Study 등록 시 모든 필드가 올바르게 저장되어야 한다.")
    void saveStudy() {
        // given
        User user = createUser();
        userRepository.save(user);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);
        Period period = new Period(startDate, endDate);
        LocalDateTime deadline = LocalDateTime.now().plusDays(5);

        Study study = Study.builder()
                .user(user)
                .title("알고리즘 스터디 모집")
                .content("백준 골드 난이도 문제 풉니다!")
                .participants(new ParticipantInfo(5))
                .skills(Set.of(Skill.JAVA, Skill.PYTHON))
                .period(period)
                .deadline(deadline)
                .build();

        // when
        Study saved = studyRepository.save(study);

        // then
        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getTitle()).isEqualTo(study.getTitle());
        assertThat(saved.getContent()).isEqualTo(study.getContent());
        assertThat(saved.getParticipants().getMaxParticipants())
                .isEqualTo(study.getParticipants().getMaxParticipants());
        assertThat(saved.getSkills()).containsExactlyInAnyOrderElementsOf(study.getSkills());
        assertThat(saved.getPeriod()).isEqualTo(study.getPeriod());
        assertThat(saved.getDeadline()).isEqualTo(study.getDeadline());
    }

    private User createUser() {
        return User.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .email("email@email.com")
                .university(University.YOUNGNAM_UNIV)
                .grade(3)
                .role(UserRoleType.ROLE_USER)
                .shortIntro("short intro")
                .position(PositionType.BACK_END)
                .skills(Set.of(Skill.JAVA, Skill.SPRING_BOOT))
                .build();
    }
}