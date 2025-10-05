package com.wagglex2.waggle.domain.assignment.service;

import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentResponseDto;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.assignment.repository.AssignmentRepository;
import com.wagglex2.waggle.domain.assignment.service.serviceImpl.AssignmentServiceImpl;
import com.wagglex2.waggle.domain.common.dto.response.ParticipantInfoResponseDto;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.University;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    @Test
    @DisplayName("저장된 과제 공고 상세 조회 성공")
    void getAssignment() {
        // given
        Assignment assignment = createAssignment();
        given(assignmentRepository.findById(1L)).willReturn(Optional.of(assignment));
        given(assignmentRepository.increaseViewCount(1L)).willReturn(1);

        // when
        AssignmentResponseDto actual = assignmentService.getAssignment(1L);

        // then
        AssignmentResponseDto expected = AssignmentResponseDto.fromEntity(assignment);
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("viewCount")
                .isEqualTo(expected);

        verify(assignmentRepository, times(1)).findById(1L);
        verify(assignmentRepository, times(1)).increaseViewCount(1L);
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