package com.wagglex2.waggle.domain.project.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.common.util.KomoranUtil;
import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.request.ProjectSearchCondition;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectDetailResponseDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import com.wagglex2.waggle.domain.project.service.ProjectService;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
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

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final KomoranUtil komoranUtil;

    @PostMapping
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
    public ResponseEntity<ApiResponse<ProjectDetailResponseDto>> getProject(@PathVariable Long projectId) {
        ProjectDetailResponseDto responseDto = projectService.getProject(projectId);

        return ResponseEntity.ok(
                ApiResponse.ok("프로젝트 공고를 성공적으로 조회하였습니다.", responseDto)
        );
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ProjectSummaryResponseDto>>> getProjectSummaries(
            @RequestParam(value = "q", required = false) String keywords,
            @RequestParam(value = "purpose", required = false) ProjectPurpose purpose,
            @RequestParam(value = "positions", required = false) List<PositionType> positions,
            @RequestParam(value = "skills", required = false) List<Skill> skills,
            @RequestParam(value = "status", required = false) RecruitmentStatus status,
            @PageableDefault(size = 9) Pageable pageable
    ) {
        Set<String> nouns = (keywords != null) ? komoranUtil.getNouns(keywords) : Set.of();
        Set<PositionType> positionSet = (positions != null) ? Set.copyOf(positions) : Set.of();
        Set<Skill> skillSet = (skills != null) ? Set.copyOf(skills) : Set.of();

        ProjectSearchCondition condition = new ProjectSearchCondition(
                nouns,
                purpose,
                positionSet,
                skillSet,
                status
        );

        Page<ProjectSummaryResponseDto> projectSummaries =
                projectService.getProjectSummaries(condition, pageable);

        return ResponseEntity.ok(
                ApiResponse.ok("프로젝트 공고 목록을 성공적으로 조회하였습니다.", projectSummaries)
        );
    }

    @PutMapping("/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> updateProject(
            @PathVariable Long projectId,
            @RequestBody @Valid ProjectUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        projectService.updateProject(userDetails.getUserId(), projectId, requestDto);

        return ResponseEntity.ok(
                ApiResponse.ok("프로젝트 공고를 성공적으로 수정하였습니다.")
        );
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        projectService.deleteProject(userDetails.getUserId(), projectId);

        return ResponseEntity.ok(
                ApiResponse.ok("프로젝트 공고를 성공적으로 삭제하였습니다.")
        );
    }
}
