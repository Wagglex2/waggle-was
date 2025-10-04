package com.wagglex2.waggle.domain.project.service;

import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectDetailResponseDto;

public interface ProjectService {
    Long createProject(Long userId, ProjectCreationRequestDto projectCreationRequestDto);
    ProjectDetailResponseDto getProject(Long projectId);
    void updateProject(Long userId, Long projectId, ProjectUpdateRequestDto updateDto);
    void deleteProject(Long userId, Long projectId);
}
