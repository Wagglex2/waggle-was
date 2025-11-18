package com.wagglex2.waggle.domain.assignment.controller;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.util.KomoranUtil;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentSearchCondition;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentUpdateRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentDetailResponseDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentSummaryResponseDto;
import com.wagglex2.waggle.domain.assignment.service.AssignmentService;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final KomoranUtil KomoranUtil;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Long>> createAssignment(
            @RequestBody @Valid AssignmentCreationRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long assignmentId = assignmentService.createAssignment(requestDto, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.ok("과제 공고를 성공적으로 등록하였습니다.", assignmentId));
    }

    @GetMapping("/{assignmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<AssignmentDetailResponseDto>> getAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AssignmentDetailResponseDto responseDto =
                assignmentService.getAssignment(userDetails.getUserId(), assignmentId);

        return ResponseEntity.ok(
                APIResponse.ok("과제 공고를 성공적으로 조회하였습니다.", responseDto)
        );
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Page<AssignmentSummaryResponseDto>>> getAssignmentSummaries(
            @RequestParam(value = "q", required = false) String keywords,
            @RequestParam(value = "grades", required = false) Set<Integer> grades,
            @RequestParam(value = "status", required = false) RecruitmentStatus status,
            @PageableDefault(size = 9) Pageable pageable
    ) {
        Set<String> nouns = (keywords != null) ? KomoranUtil.getNouns(keywords) : Set.of();
        Set<Integer> gradeSet = (grades != null) ? Set.copyOf(grades) : Set.of();

        AssignmentSearchCondition condition = new AssignmentSearchCondition(
                nouns,
                gradeSet,
                status
        );

        Page<AssignmentSummaryResponseDto> assignmentSummaries =
                assignmentService.getAssignmentSummaries(condition, pageable);

        return ResponseEntity.ok(
                APIResponse.ok("과제 공고 목록을 성공적으로 조회하였습니다.", assignmentSummaries)
        );
    }

    @PutMapping("/{assignmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody @Valid AssignmentUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        assignmentService.updateAssignment(userDetails.getUserId(), assignmentId, requestDto);

        return ResponseEntity.ok(
                APIResponse.ok("과제 공고를 성공적으로 수정하였습니다.")
        );
    }


    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> deleteAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        assignmentService.deleteAssignment(userDetails.getUserId(), assignmentId);

        return ResponseEntity.ok(
                APIResponse.ok("과제 공고를 성공적으로 삭제하였습니다.")
        );
    }

}
