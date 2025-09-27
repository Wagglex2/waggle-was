package com.wagglex2.waggle.domain.project.service;

import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;

public interface ProjectService {
    void createProject(Long userId, ProjectCreationRequestDto projectCreationRequestDto);
}
