package com.wagglex2.waggle.domain.team.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.team.dto.response.TeamResponseDto;
import com.wagglex2.waggle.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;


    /**
     * 사용자 본인의 팀 목록을 카테고리 및 상태별로 조회하는 컨트롤러 메서드.
     *
     * <ul>
     *   <li>현재 로그인한 사용자의 ID를 기반으로, 본인이 속한 팀 목록을 조회한다.</li>
     *   <li>카테고리(프로젝트/스터디/과제)와 모집 상태(모집 중/마감 등)에 따라 필터링한다.</li>
     * </ul>
     * </p>
     *
     * @param category      모집 카테고리 (PROJECT, STUDY, ASSIGNMENT) — 기본값: PROJECT
     * @param status        모집 상태 (RECRUITING, CLOSED, CANCELED) — 기본값: RECRUITING
     * @param pageable      페이지 및 정렬 정보 (기본: 3개씩, createdAt DESC)
     * @return              조회된 팀 목록(Page 형태)
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<TeamResponseDto>>> getMyTeamByCategory(
            @RequestParam(value = "category", defaultValue = "PROJECT") RecruitmentCategory category,
            @RequestParam(value = "status", defaultValue = "RECRUITING") RecruitmentStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(
                    size = 3,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {

        Page<TeamResponseDto> data = teamService.getByUserIdAndCategoryAndStatus(
                userDetails.getUserId(),
                category,
                status,
                pageable
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("팀 조회에 성공했습니다.", data));
    }
}
