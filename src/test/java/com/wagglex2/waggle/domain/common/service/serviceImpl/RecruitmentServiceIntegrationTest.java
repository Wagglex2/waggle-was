package com.wagglex2.waggle.domain.common.service.serviceImpl;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.repository.RecruitmentRepository;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.repository.ProjectRepository;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import com.wagglex2.waggle.domain.user.entity.type.UserRoleType;
import com.wagglex2.waggle.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class RecruitmentServiceIntegrationTest {

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private RecruitmentRepository recruitmentRepository;
    
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = createUser();
        userRepository.save(user);
    }

    @Test
    @DisplayName("마감일이 지난 공고 상태 'CLOSED'로 변경")
    void closeExpiredRecruitments() {
        // given
        saveRecruitments();  // 마감일이 지난 공고와 지나지 않은 공고 각각 5개씩 저장

        List<BaseRecruitment> expired = recruitmentRepository.findByStatusIsAndDeadlineBefore(
                RecruitmentStatus.RECRUITING,
                LocalDateTime.now()
        );

        // 마감일이 지난 공고 확인
        expired.forEach(e -> {
            assertThat(e.getStatus()).isEqualTo(RecruitmentStatus.RECRUITING);
            assertThat(e.getDeadline()).isBefore(LocalDateTime.now());
        });

        // when
        recruitmentService.closeExpiredRecruitments();
        recruitmentRepository.flush();

        // then
        List<BaseRecruitment> updated = recruitmentRepository.findByStatusIsAndDeadlineBefore(
                RecruitmentStatus.CLOSED,
                LocalDateTime.now()
        );

        // 상태 변경 확인
        updated.forEach(u -> {
            assertThat(u.getStatus()).isEqualTo(RecruitmentStatus.CLOSED);
            assertThat(u.getDeadline()).isBefore(LocalDateTime.now());
        });

        // 상태 변경 데이터 개수 일치 확인
        // 실제 DB 연동 테스트이기 때문에 5개 보다 많을 수 있음
        assertThat(updated.size()).isEqualTo(expired.size());
    }

    void saveRecruitments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recruitingDeadline = now
                                            .plusDays(3)
                                            .withHour(23)
                                            .withMinute(59)
                                            .withSecond(59);

        LocalDateTime expiredDeadline = now
                                            .minusDays(3)
                                            .withHour(23)
                                            .withMinute(59)
                                            .withSecond(59);

        // 마감일이 지나지 않은 공고 5개 저장
        for (int i = 0; i < 5; i++) {
            projectRepository.save(createProject(recruitingDeadline));
        }

        // 마감일이 지난 공고 5개 저장
        for (int i = 0; i < 5; i++) {
            projectRepository.save(createProject(expiredDeadline));
        }

        projectRepository.flush();
    }

    private Project createProject(LocalDateTime deadline) {
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now().plusDays(3);
        Period period = new Period(startDate, endDate);

        return Project.builder()
                .user(user)
                .title("해커톤 팀원 구합니다.")
                .content("카카오에서 개최하는 해커톤 같이 할 사람 구해요.")
                .purpose(ProjectPurpose.HACKATHON)
                .meetingType(MeetingType.HYBRID)
                .positions(new HashSet<>(Set.of(
                        new PositionParticipantInfo(PositionType.FRONT_END, new ParticipantInfo(3)),
                        new PositionParticipantInfo(PositionType.BACK_END, new ParticipantInfo(3))
                )))
                .skills(new HashSet<>(Set.of(Skill.REACT, Skill.SPRING_BOOT)))
                .grades(new HashSet<>(Set.of(3)))
                .period(period)
                .deadline(deadline)
                .build();
    }

    private User createUser() {
        return User.builder()
                .username("abc123")
                .password("pw")
                .nickname("솔랑솔랑")
                .email("abc123@waggle.com")
                .university(University.YOUNGNAM_UNIV)
                .grade(3)
                .role(UserRoleType.ROLE_USER)
                .shortIntro("Hi")
                .position(PositionType.FRONT_END)
                .skills(Set.of(Skill.REACT, Skill.SPRING_BOOT))
                .build();
    }
}
