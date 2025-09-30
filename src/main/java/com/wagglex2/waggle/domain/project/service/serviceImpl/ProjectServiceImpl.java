package com.wagglex2.waggle.domain.project.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectResponseDto;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.repository.ProjectRepository;
import com.wagglex2.waggle.domain.project.service.ProjectService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Transactional
    @Override
    public void createProject(Long userId, ProjectCreationRequestDto requestDto) {
        requestDto.validate();
        User user = userService.findById(userId);
        Project newProject = ProjectCreationRequestDto.toEntity(user, requestDto);
        projectRepository.save(newProject);
    }

    @Transactional
    @Override
    public ProjectResponseDto getProject(Long projectId) {
        int updated = projectRepository.increaseViewCount(projectId);

        if (updated == 0) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }

        return projectRepository.findById(projectId)
                .map(ProjectResponseDto::fromEntity).get();
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

        if (project.getStatus() == RecruitmentStatus.CANCELED) {
            return;
        }

        project.update(updateDto);
    }
}
