package com.wagglex2.waggle.domain.assignment.service;

import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;

public interface AssignmentService {
    Long createAssignment(AssignmentCreationRequestDto assignmentCreationRequestDto, Long userId);
}
