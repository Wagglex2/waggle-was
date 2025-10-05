package com.wagglex2.waggle.domain.assignment.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentResponseDto;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.assignment.repository.AssignmentRepository;
import com.wagglex2.waggle.domain.assignment.service.AssignmentService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
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

        return assignmentRepository.findById(assignmentId)
                .map(AssignmentResponseDto::fromEntity).get();
    }
}
