package com.wagglex2.waggle.domain.assignment.service;

import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentUpdateRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentResponseDto;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;

public interface AssignmentService {
    Long createAssignment(AssignmentCreationRequestDto assignmentCreationRequestDto, Long userId);
    AssignmentResponseDto getAssignment(Long assignmentId);
    void updateAssignment(Long userId, Long assignmentId, AssignmentUpdateRequestDto updateDto);
}
