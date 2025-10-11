package com.wagglex2.waggle.domain.assignment.service;

import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentUpdateRequestDto;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.assignment.repository.AssignmentRepository;
import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.ParticipantInfoUpdateRequestDto;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class AssignmentServiceIntegrationTest {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = createUser();
        userRepository.save(user);

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("과제 공고 수정 시 정상적으로 업데이트 된다.")
    void updateAssignment() throws InterruptedException {
        // given
        Assignment assignment = createAssignment();
        assignmentRepository.save(assignment);

        Long userId = assignment.getUser().getId();
        Long assignmentId = assignment.getId();

        LocalDateTime beforeUpdatedAt = assignment.getUpdatedAt();
        RecruitmentStatus beforeStatus = assignment.getStatus();

        assertThat(beforeStatus).isEqualTo(RecruitmentStatus.RECRUITING);

        AssignmentUpdateRequestDto updateDto = createUpdateDto();

        // when
        Thread.sleep(3000);
        assignmentService.updateAssignment(userId, assignmentId, updateDto);
        assignmentRepository.flush();

        // then
        assertThat(assignment.getTitle()).isEqualTo(updateDto.getTitle());
        assertThat(assignment.getContent()).isEqualTo(updateDto.getContent());
        assertThat(assignment.getDepartment()).isEqualTo(updateDto.getDepartment());
        assertThat(assignment.getLecture()).isEqualTo(updateDto.getLecture());
        assertThat(assignment.getLectureCode()).isEqualTo(updateDto.getLectureCode());
        assertThat(assignment.getParticipants().getMaxParticipants())
                .isEqualTo(updateDto.getParticipants().maxParticipants());
        assertThat(assignment.getGrades()).containsExactlyInAnyOrder(4);

        assertThat(assignment.getUpdatedAt()).isAfter(beforeUpdatedAt);
        assertThat(assignment.getStatus()).isEqualTo(RecruitmentStatus.CLOSED);
    }

    private AssignmentUpdateRequestDto createUpdateDto() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(1)
                .withHour(23).withMinute(59).withSecond(59);

        ParticipantInfoUpdateRequestDto participants =
                new ParticipantInfoUpdateRequestDto(8, 3); // 최대 8명, 현재 3명

        return new AssignmentUpdateRequestDto(
                "수정된 제목",
                "수정된 본문",
                deadline,
                "소프트웨어학과",
                "운영체제",
                "CSE301",
                participants,
                Set.of(new GradeRequestDto(4))
        );
    }

    private Assignment createAssignment() {
        LocalDateTime deadline = LocalDateTime.now().plusDays(2)
                .withHour(23).withMinute(59).withSecond(59);

        return Assignment.builder()
                .user(user)
                .title("네트워크 과제 같이 하실 분 구합니다.")
                .content("TCP/IP 보고서 같이 작성하실 분 구해요.")
                .department("컴퓨터공학과")
                .lecture("컴퓨터 네트워크")
                .lectureCode("CSE302")
                .participants(new ParticipantInfo(5))
                .grades(new HashSet<>(Set.of(3)))
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