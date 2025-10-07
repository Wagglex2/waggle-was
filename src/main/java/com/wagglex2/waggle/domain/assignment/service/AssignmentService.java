package com.wagglex2.waggle.domain.assignment.service;

import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentResponseDto;

public interface AssignmentService {
    Long createAssignment(AssignmentCreationRequestDto assignmentCreationRequestDto, Long userId);
    AssignmentResponseDto getAssignment(Long assignmentId);
}
