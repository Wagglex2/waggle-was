package com.wagglex2.waggle.domain.project.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectResponseDto;
import com.wagglex2.waggle.domain.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> createProject(
            @RequestBody @Valid ProjectCreationRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long projectId = projectService.createProject(userDetails.getUserId(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("프로젝트 공고를 성공적으로 등록하였습니다.", projectId));
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProjectResponseDto>> getProject(@PathVariable Long projectId) {
        ProjectResponseDto responseDto = projectService.getProject(projectId);

        return ResponseEntity.ok(
                ApiResponse.ok("프로젝트 공고를 성공적으로 조회하였습니다.", responseDto)
        );
    }
}
