package com.wagglex2.waggle.domain.review.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰를 작성한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>요청 본문 DTO 검증 (@Valid)</li>
     *   <li>인증 사용자 정보 추출 (@AuthenticationPrincipal)</li>
     *   <li>ReviewService 호출하여 리뷰 생성</li>
     *   <li>생성된 리뷰 ID 반환</li>
     * </ol>
     *
     * @param dto         후기 작성 요청 DTO
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 리뷰 ID를 포함한 ApiResponse
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> createReview(
            @Valid @RequestBody ReviewCreationRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long reviewId = reviewService.createReview(userDetails.getUserId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("리뷰 작성에 성공했습니다.", reviewId));
    }
}
