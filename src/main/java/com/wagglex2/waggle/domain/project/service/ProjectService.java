package com.wagglex2.waggle.domain.project.service;

import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectResponseDto;

public interface ProjectService {
    Long createProject(Long userId, ProjectCreationRequestDto projectCreationRequestDto);
    ProjectResponseDto getProject(Long projectId);
}
