package com.wagglex2.waggle.domain.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.domain.study.dto.response.StudyResponseDto;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.study.service.StudyService;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.Period;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudyController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudyService studyService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("스터디 공고 상세 조회 성공")
    void getStudy() throws Exception {
        // given
        Study study = createStudy();
        StudyResponseDto responseDto = StudyResponseDto.fromEntity(study);
        given(studyService.getStudy(1L, 1L)).willReturn(responseDto);

        // when
        String responseJson = mockMvc.perform(
                        get("/api/v1/studies/1")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        // code, message 제외하고 data만 비교
        String actualJson = objectMapper.readTree(responseJson).get("data").toString();
        String expectedJson = objectMapper.writeValueAsString(responseDto);
        assertThat(actualJson).isEqualTo(expectedJson);
    }

    private Study createStudy() {
        User author = mock(User.class);
        given(author.getId()).willReturn(1L);
        given(author.getNickname()).willReturn("홍길동");
        given(author.getUniversity()).willReturn(University.YOUNGNAM_UNIV);

        // Period 는 내부 생성자가 공개 안됐을 수 있어 mock 처리 (DTO 변환은 null 필드 허용)
        Period period = mock(Period.class);

        return Study.builder()
                .user(author)
                .title("Spring Boot 스터디 모집합니다.")
                .content("Spring Boot 백엔드 스터디를 진행합니다.")
                .deadline(LocalDate.of(2025, 11, 1).atStartOfDay())
                .participants(new ParticipantInfo(5))
                .period(period)
                .skills(Set.of()) // 필요시 실제 Skill enum 넣어도 됨
                .build();
    }
}