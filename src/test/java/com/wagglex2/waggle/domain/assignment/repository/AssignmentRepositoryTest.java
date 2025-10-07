package com.wagglex2.waggle.domain.assignment.repository;

import com.wagglex2.waggle.common.config.JpaAuditingConfig;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import com.wagglex2.waggle.domain.user.entity.type.UserRoleType;
import com.wagglex2.waggle.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingConfig.class)
@DataJpaTest
class AssignmentRepositoryTest {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Assignment 등록 시 모든 필드가 올바르게 저장되어야 한다.")
    void saveAssignment() {
        // given
        User user = createUser();
        userRepository.save(user);

        Assignment assignment = Assignment.builder()
                .user(user)
                .title("데이터베이스 팀플 모집")
                .content("DB 과제 같이 하실 분 구합니다.")
                .deadline(LocalDateTime.now().plusDays(7))
                .department("컴퓨터공학과")
                .lecture("데이터베이스")
                .lectureCode("DB101")
                .participants(new ParticipantInfo(5))
                .grades(Set.of(2, 3, 4))
                .build();

        // when
        Assignment saved = assignmentRepository.save(assignment);

        // then
        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getTitle()).isEqualTo("데이터베이스 팀플 모집");
        assertThat(saved.getDepartment()).isEqualTo("컴퓨터공학과");
        assertThat(saved.getLecture()).isEqualTo("데이터베이스");
        assertThat(saved.getLectureCode()).isEqualTo("DB101");
        assertThat(saved.getParticipants().getMaxParticipants()).isEqualTo(5);
        assertThat(saved.getGrades()).containsExactlyInAnyOrder(2, 3, 4);
        assertThat(saved.getViewCount()).isEqualTo(0); // 기본값 확인
    }

    @Test
    @DisplayName("과제 id로 조회 성공")
    void findById() {
        // given
        Assignment assignment = createAssignment();
        assignmentRepository.save(assignment);

        // when
        Optional<Assignment> found = assignmentRepository.findById(assignment.getId());

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get().getLecture()).isEqualTo("데이터베이스");
    }

    @Test
    @DisplayName("조회수 증가 쿼리가 정상적으로 반영되어야 한다.")
    void increaseViewCount() {
        // given
        Assignment assignment = createAssignment();
        assignmentRepository.save(assignment);

        //when
        assignmentRepository.increaseViewCount(assignment.getId());
        assignmentRepository.increaseViewCount(assignment.getId());

        // then
        Assignment updated = assignmentRepository.findById(assignment.getId()).get();
        assertThat(updated.getViewCount()).isEqualTo(2);
    }

    private Assignment createAssignment() {
        User user = createUser();
        userRepository.save(user);

        return Assignment.builder()
                .user(user)
                .title("데이터베이스 팀플 모집")
                .content("DB 과제 같이 하실 분 구합니다.")
                .deadline(LocalDateTime.now().plusDays(7))
                .department("컴퓨터공학과")
                .lecture("데이터베이스")
                .lectureCode("DB101")
                .participants(new ParticipantInfo(5))
                .grades(Set.of(2, 3, 4))
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
                .shortIntro("짧은 소개글")
                .position(com.wagglex2.waggle.domain.common.type.PositionType.FRONT_END)
                .skills(Set.of(com.wagglex2.waggle.domain.common.type.Skill.JAVA))
                .build();
    }
}