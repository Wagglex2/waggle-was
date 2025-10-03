package com.wagglex2.waggle.domain.assignment.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> createAssignment(
            @RequestBody @Valid AssignmentCreationRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long assignmentId = assignmentService.createAssignment(requestDto, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("과제 공고를 성공적으로 등록하였습니다.", assignmentId));
    }
}
