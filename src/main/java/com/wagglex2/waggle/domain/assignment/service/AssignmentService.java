package com.wagglex2.waggle.domain.assignment.service;

import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentSearchCondition;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentUpdateRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentDetailResponseDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssignmentService {
    Long createAssignment(AssignmentCreationRequestDto assignmentCreationRequestDto, Long userId);
    AssignmentDetailResponseDto getAssignment(Long viewerId, Long assignmentId);
    Page<AssignmentSummaryResponseDto> getAssignmentSummaries(AssignmentSearchCondition condition, Pageable pageable);
    void updateAssignment(Long userId, Long assignmentId, AssignmentUpdateRequestDto updateDto);
    void deleteAssignment(Long userId, Long assignmentId);
}
