package com.wagglex2.waggle.domain.project.repository;

import com.wagglex2.waggle.common.config.JpaAuditingConfig;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/*
    Repository 관련 Bean만 로드하기 때문에,
    JpaAuditingConfig를 import하지 않으면 @CreatedDate, @LastModifiedDate 등이 적용되지 않아
    createdAt, updatedAt 필드가 null이 되어, 테스트 정상 진행 불가
*/
@Import(JpaAuditingConfig.class)
@DataJpaTest
class ProjectRepositoryTest {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Project 등록 시 모든 필드가 올바르게 저장되어야 한다.")
    void saveProject() {
        // given
        User user = createUser();
        userRepository.save(user);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        Period period = new Period(startDate, endDate);
        LocalDateTime deadline = LocalDateTime.now().minusDays(3);

        Project project = Project.builder()
                .user(user)
                .title("해커톤 팀원 구합니다.")
                .content("카카오에서 개최하는 해커톤 같이 할 사람 구해요.")
                .purpose(ProjectPurpose.HACKATHON)
                .meetingType(MeetingType.HYBRID)
                .positions(Set.of(
                        new PositionParticipantInfo(PositionType.FRONT_END, new ParticipantInfo(3)),
                        new PositionParticipantInfo(PositionType.BACK_END, new ParticipantInfo(3))
                ))
                .skills(Set.of(Skill.REACT, Skill.SPRING_BOOT))
                .grades(Set.of(3, 4))
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

    @Test
    @DisplayName("프로젝트 id로 프로젝트 공고 조회 성공")
    void findById() {
        // given
        Project project = createProject();
        projectRepository.save(project);

        // when
        Optional<Project> found = projectRepository.findById(project.getId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get()).usingRecursiveComparison().isEqualTo(project);
    }

    @Test
    @DisplayName("조회수 증가 쿼리가 정상적으로 반영되어야 한다.")
    void increaseViewCount() throws InterruptedException {
        // given
        Project project = createProject();
        projectRepository.save(project);

        // when
        projectRepository.increaseViewCount(project.getId());
        projectRepository.increaseViewCount(project.getId());


        // then
        Project updated = projectRepository.findById(project.getId()).get();
        assertThat(updated.getViewCount()).isEqualTo(2);
    }

    private Project createProject() {
        User user = createUser();
        userRepository.save(user);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(10);
        Period period = new Period(startDate, endDate);
        LocalDateTime deadline = LocalDateTime.now().minusDays(3);

        return Project.builder()
                .user(user)
                .title("해커톤 팀원 구합니다.")
                .content("카카오에서 개최하는 해커톤 같이 할 사람 구해요.")
                .purpose(ProjectPurpose.HACKATHON)
                .meetingType(MeetingType.HYBRID)
                .positions(Set.of(
                        new PositionParticipantInfo(PositionType.FRONT_END, new ParticipantInfo(3)),
                        new PositionParticipantInfo(PositionType.BACK_END, new ParticipantInfo(3))
                ))
                .skills(Set.of(Skill.REACT, Skill.SPRING_BOOT))
                .grades(Set.of(3, 4))
                .period(period)
                .deadline(deadline)
                .build();
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
                .position(PositionType.FRONT_END)
                .skills(Set.of(Skill.REACT, Skill.SPRING_BOOT))
                .build();

    }
}
