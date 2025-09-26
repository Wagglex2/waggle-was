package com.wagglex2.waggle.domain.assignment.service.serviceImpl;

import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.assignment.repository.AssignmentRepository;
import com.wagglex2.waggle.domain.assignment.service.AssignmentService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final UserService userService;

    @Override
    public void createAssignment(AssignmentCreationRequestDto requestDto, Long userId) {
        // requestDto.validate();
        User user = userService.findById(userId);
        Assignment newAssignment = AssignmentCreationRequestDto.toEntity(user, requestDto);
        assignmentRepository.save(newAssignment);
    }
}
