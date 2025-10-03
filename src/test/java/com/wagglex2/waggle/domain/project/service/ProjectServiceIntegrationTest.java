package com.wagglex2.waggle.domain.project.service;

import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.ParticipantInfoUpdateRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PeriodRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PositionInfoUpdateRequestDto;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.repository.ProjectRepository;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import com.wagglex2.waggle.domain.user.entity.type.UserRoleType;
import com.wagglex2.waggle.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProjectServiceIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = createUser();userRepository.save(user);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("프로젝트 공고 수정 시 정상적으로 업데이트 된다.")
    void updateProject() throws InterruptedException {
        // given
        Project project = createProject();
        projectRepository.save(project);

        Long userId = project.getUser().getId();
        Long projectId = project.getId();

        LocalDateTime beforeUpdatedAt = project.getUpdatedAt();
        RecruitmentStatus beforeStatus = project.getStatus();

        // 수정 전 상태 확인
        assertThat(beforeStatus).isEqualTo(RecruitmentStatus.RECRUITING);

        ProjectUpdateRequestDto updateDto = createUpdateDto();

        // when
        Thread.sleep(3000);
        projectService.updateProject(userId, projectId, updateDto);
        projectRepository.flush();  // DB에 반영

        // then
        // 데이터 변경 확인
        assertThat(project).usingRecursiveComparison()
                .ignoringFields("id", "user", "category", "grades",
                        "createdAt", "updatedAt", "viewCount", "status")
                .isEqualTo(updateDto);

        // 수정일이 갱신되었는지 확인
        assertThat(project.getUpdatedAt()).isAfter(beforeUpdatedAt);

        // 공고 상태가 "CLOSED"로 바뀌었는지 확인
        assertThat(project.getStatus()).isEqualTo(RecruitmentStatus.CLOSED);
    }

    @Test
    @DisplayName("프로젝트 공고 삭제 시, 상태가 'CANCELED'로 바뀌어야 한다.")
    void cancelProject() {
        // given
        Project project = createProject();
        projectRepository.save(project);

        Long userId = project.getUser().getId();
        Long projectId = project.getId();
        RecruitmentStatus beforeStatus = project.getStatus();

        // 삭제 전 상태 확인
        assertThat(beforeStatus).isEqualTo(RecruitmentStatus.RECRUITING);

        // when
        projectService.deleteProject(userId, projectId);

        // then
        assertThat(project.getStatus()).isEqualTo(RecruitmentStatus.CANCELED);
    }

    private ProjectUpdateRequestDto createUpdateDto() {
        // 마감일이 '오늘'보다 빠름 -> 수정 시 status가 "CLOSED"로 바뀌어야 함
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now().plusDays(3);
        PeriodRequestDto periodDto = new PeriodRequestDto(startDate, endDate);
        LocalDateTime deadline = LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59);

        return new ProjectUpdateRequestDto(
                "수정된 제목",
                "수정된 본문",
                deadline,
                ProjectPurpose.CONTEST,
                MeetingType.HYBRID,
                Set.of(Skill.REACT, Skill.SPRING_BOOT),
                Set.of(new GradeRequestDto(3)),
                periodDto,
                Set.of(new PositionInfoUpdateRequestDto(PositionType.FULL_STACK, new ParticipantInfoUpdateRequestDto(6, 2)))
        );
    }

    private Project createProject() {
        // 마감일이 '오늘'보다 늦음
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now().plusDays(3);
        Period period = new Period(startDate, endDate);
        LocalDateTime deadline = LocalDateTime.now().plusDays(1).withHour(23).withMinute(59).withSecond(59);

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
