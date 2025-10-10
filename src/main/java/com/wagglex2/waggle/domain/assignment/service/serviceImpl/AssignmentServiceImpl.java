package com.wagglex2.waggle.domain.assignment.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentUpdateRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentResponseDto;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.assignment.repository.AssignmentRepository;
import com.wagglex2.waggle.domain.assignment.service.AssignmentService;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
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
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final UserService userService;

    @Transactional
    @Override
    public Long createAssignment(AssignmentCreationRequestDto requestDto, Long userId) {
        // requestDto.validate();
        User user = userService.findById(userId);
        Assignment newAssignment = AssignmentCreationRequestDto.toEntity(user, requestDto);

        return assignmentRepository.save(newAssignment).getId();
    }

    @Transactional
    @Override
    public AssignmentResponseDto getAssignment(Long assignmentId) {
        int updated = assignmentRepository.increaseViewCount(assignmentId);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        return AssignmentResponseDto.fromEntity(assignment);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void updateAssignment(
            @P("userId") Long userId, Long assignmentId, AssignmentUpdateRequestDto updateDto
    ) {
        // updateDto.validate();
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        if (!userId.equals(assignment.getUser().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (assignment.getStatus() == RecruitmentStatus.CANCELED) {
            return;
        }

        assignment.update(updateDto);
    }
}
