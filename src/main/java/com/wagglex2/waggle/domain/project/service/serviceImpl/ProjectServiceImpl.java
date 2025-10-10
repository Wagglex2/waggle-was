package com.wagglex2.waggle.domain.project.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.dto.response.PositionInfoResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.request.ProjectSearchCondition;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectDetailResponseDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.repository.ProjectRepository;
import com.wagglex2.waggle.domain.project.service.ProjectService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Transactional
    @Override
    public Long createProject(Long userId, ProjectCreationRequestDto requestDto) {
        requestDto.validate();
        User user = userService.findById(userId);
        Project newProject = ProjectCreationRequestDto.toEntity(user, requestDto);

        return projectRepository.save(newProject).getId();
    }

    @Transactional
    @Override
    public ProjectDetailResponseDto getProject(Long projectId) {
        int updated = projectRepository.increaseViewCount(projectId);

        if (updated == 0) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }

        // 각 collection 정보는 추가 쿼리로 조회 후 dto에 추가
        // Project 정보와 User 정보
        ProjectDetailResponseDto responseDto = projectRepository.findByIdWithUser(projectId)
                .map(ProjectDetailResponseDto::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // Position 정보
        Set<PositionInfoResponseDto> positions = projectRepository.findPositionsByProjectId(projectId).stream()
                .map(PositionInfoResponseDto::from)
                .collect(Collectors.toSet());

        // Skill 정보
        Set<Skill> skills = projectRepository.findSkillsByProjectId(projectId);

        // Grade 정보
        Set<Integer> grades = projectRepository.findGradesByProjectId(projectId);

        responseDto.setPositions(positions);
        responseDto.setSkills(skills);
        responseDto.setGrades(grades);

        return responseDto;
    }

    @Override
    public Page<ProjectSummaryResponseDto> getProjectSummaries(
            ProjectSearchCondition condition,
            Pageable pageable
    ) {
        return projectRepository.findProjectSummaries(condition, pageable);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void updateProject(@P("userId") Long userId, Long projectId, ProjectUpdateRequestDto updateDto) {
        updateDto.validate();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // 권한 검증
        if (!userId.equals(project.getUser().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 삭제 여부 검증
        if (project.getStatus() == RecruitmentStatus.CANCELED) {
            return;
        }

        project.update(updateDto);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void deleteProject(@P("userId") Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // 권한 검증
        if (!userId.equals(project.getUser().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 논리적 삭제
        project.cancel();
    }
}
