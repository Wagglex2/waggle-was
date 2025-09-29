package com.wagglex2.waggle.domain.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.project.dto.response.ProjectResponseDto;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.service.ProjectService;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/*
    MVC 관련 Bean만 로드하기 때문에,
    @EnableJpaAuditing이 @SpringBootApplication과 동일 클래스에 있을 경우,
    JPA 관련 Bean이 등록되지 않은 상태로 Auditing이 적용되면서
    `JPA metamodel must not be empty!` 오류가 발생되어 테스트 정상 진행 불가
*/
@WebMvcTest(ProjectController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 제거
class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("프로젝트 공고 조회 성공")
    void getProject() throws Exception {
        // given
        Project project = createProject();
        ProjectResponseDto responseDto = ProjectResponseDto.fromEntity(project);
        given(projectService.getProject(1L)).willReturn(responseDto);

        // when
        String responseJson = mockMvc.perform(
                get("/api/v1/projects/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        // code, message 제외, data만 추출
        String actualJson = objectMapper.readTree(responseJson).get("data").toString();
        String expectedJson = objectMapper.writeValueAsString(responseDto);
        assertThat(actualJson).isEqualTo(expectedJson);
    }

    private Project createProject() {
        User author = mock(User.class);
        given(author.getId()).willReturn(1L);
        given(author.getNickname()).willReturn("솔랑솔랑");

        LocalDate startDate = LocalDate.of(2025, 9, 10);
        LocalDate endDate = startDate.plusDays(10);
        Period period = new Period(startDate, endDate);
        LocalDateTime deadline = LocalDateTime.of(startDate, LocalTime.of(23, 59, 59));

        return Project.builder()
                .user(author)
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
}