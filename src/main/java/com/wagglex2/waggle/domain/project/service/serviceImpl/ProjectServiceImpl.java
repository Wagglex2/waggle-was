package com.wagglex2.waggle.domain.project.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectResponseDto;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.repository.ProjectRepository;
import com.wagglex2.waggle.domain.project.service.ProjectService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Override
    public void createProject(Long userId, ProjectCreationRequestDto requestDto) {
        requestDto.validate();
        User user = userService.findById(userId);
        Project newProject = ProjectCreationRequestDto.toEntity(user, requestDto);
        projectRepository.save(newProject);
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectResponseDto getProject(Long projectId) {
        ProjectResponseDto responseDto = projectRepository.findById(projectId)
                .map(ProjectResponseDto::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCode.RECRUITMENT_NOT_FOUND));

        projectRepository.increaseViewCount(projectId);
        responseDto.increaseViewCount();  // 조회 요청도 조회수에 포함

        return responseDto;
    }
}
