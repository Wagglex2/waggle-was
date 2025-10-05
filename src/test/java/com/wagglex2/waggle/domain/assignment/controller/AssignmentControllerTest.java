package com.wagglex2.waggle.domain.assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentResponseDto;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.assignment.service.AssignmentService;
import com.wagglex2.waggle.domain.common.dto.response.ParticipantInfoResponseDto;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
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

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AssignmentController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssignmentService assignmentService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("과제 공고 상세 조회 성공")
    void getAssignment() throws Exception {
        // given
        Assignment assignment = createAssignment();
        AssignmentResponseDto responseDto = AssignmentResponseDto.fromEntity(assignment);
        given(assignmentService.getAssignment(1L)).willReturn(responseDto);

        // when
        String responseJson = mockMvc.perform(
                        get("/api/v1/assignments/1")
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

    private Assignment createAssignment() {
        User author = mock(User.class);
        given(author.getId()).willReturn(1L);
        given(author.getNickname()).willReturn("박대형");
        given(author.getUniversity()).willReturn(University.YOUNGNAM_UNIV);

        return Assignment.builder()
                .user(author)
                .title("네트워크 과제 같이 하실 분 구합니다.")
                .content("TCP/IP 정리 ppt 같이 만들 분 구해요.")
                .department("컴퓨터공학과")
                .lecture("컴퓨터 네트워크")
                .lectureCode("CSE302")
                .participants(new ParticipantInfo(4))
                .grades(Set.of(3, 4))
                .deadline(LocalDateTime.now().plusDays(5))
                .build();
    }
}