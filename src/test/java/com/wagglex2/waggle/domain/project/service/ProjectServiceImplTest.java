package com.wagglex2.waggle.domain.project.service;

import com.wagglex2.waggle.domain.common.dto.response.PositionInfoResponseDto;
import com.wagglex2.waggle.domain.common.type.*;
import com.wagglex2.waggle.domain.project.dto.response.ProjectDetailResponseDto;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.repository.ProjectRepository;
import com.wagglex2.waggle.domain.project.service.serviceImpl.ProjectServiceImpl;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    @DisplayName("저장된 프로젝트 공고 조회 성공")
    void getProject() {
        // given
        Project project = createProject();
        given(projectRepository.findByIdWithUser(1L)).willReturn(Optional.of(project));
        given(projectRepository.findPositionsByProjectId(1L)).willReturn(project.getPositions());
        given(projectRepository.findSkillsByProjectId(1L)).willReturn(project.getSkills());
        given(projectRepository.findGradesByProjectId(1L)).willReturn(project.getGrades());
        given(projectRepository.increaseViewCount(1L)).willReturn(1);

        // when
        ProjectDetailResponseDto actual = projectService.getProject(1L);

        // then
        ProjectDetailResponseDto expected = ProjectDetailResponseDto.fromEntity(project);
        expected.setPositions(project.getPositions().stream()
                .map(PositionInfoResponseDto::from).collect(Collectors.toSet()));
        expected.setSkills(project.getSkills());
        expected.setGrades(project.getGrades());

        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("viewCount")
                .isEqualTo(expected);

        verify(projectRepository, times(1)).findByIdWithUser(1L);
        verify(projectRepository, times(1)).findPositionsByProjectId(1L);
        verify(projectRepository, times(1)).findSkillsByProjectId(1L);
        verify(projectRepository, times(1)).findGradesByProjectId(1L);
        verify(projectRepository, times(1)).increaseViewCount(1L);
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