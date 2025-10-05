package com.wagglex2.waggle.domain.review.controller;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import com.wagglex2.waggle.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 로그인한 사용자가 작성한 리뷰 목록을 조회한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>요청한 페이지 번호(pageNo)가 1 미만인 경우 {@code BusinessException} 발생</li>
     *   <li>로그인한 사용자 ID를 가져와(reviewerId) {@code reviewService.getReviewsByReviewerId()} 호출</li>
     *   <li>서비스 단에서는 0 기반 페이지 번호를 사용하므로 {@code pageNo - 1} 전달</li>
     *   <li>조회 결과(Page<ReviewResponseDto>)를 {@code ApiResponse} 형태로 감싸서 응답</li>
     * </ol>
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param pageNo      요청 페이지 번호 (1부터 시작, 기본값 1)
     * @return 리뷰 목록(Page<ReviewResponseDto>)을 포함한 200 OK 응답
     * @throws BusinessException pageNo가 1 미만일 경우 INVALID_PAGE_NUMBER 예외 발생
     */
    @GetMapping("/me/written")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ReviewResponseDto>>> getMyWrittenReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "page", required = false, defaultValue = "1") int pageNo
    ) {
        if (pageNo < 1) {
            throw new BusinessException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        Page<ReviewResponseDto> data = reviewService.getReviewsByReviewerId(
                userDetails.getUserId(),
                pageNo - 1
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("내가 작성한 리뷰 조회에 성공했습니다.", data));
    }

    /**
     * 로그인한 사용자가 받은 리뷰 목록을 조회한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>요청한 페이지 번호(pageNo)가 1 미만인 경우 {@code BusinessException} 발생</li>
     *   <li>로그인한 사용자 ID를 가져와(revieweeId) {@code reviewService.getReviewsByRevieweeId()} 호출</li>
     *   <li>서비스 단에서는 0 기반 페이지 번호를 사용하므로 {@code pageNo - 1} 전달</li>
     *   <li>조회 결과(Page<ReviewResponseDto>)를 {@code ApiResponse} 형태로 감싸서 응답</li>
     * </ol>
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param pageNo      요청 페이지 번호 (1부터 시작, 기본값 1)
     * @return 리뷰 목록(Page<ReviewResponseDto>)을 포함한 200 OK 응답
     * @throws BusinessException pageNo가 1 미만일 경우 INVALID_PAGE_NUMBER 예외 발생
     */
    @GetMapping("/me/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ReviewResponseDto>>> getMyReceivedReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "page", required = false, defaultValue = "1") int pageNo
    ) {
        if (pageNo < 1) {
            throw new BusinessException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        Page<ReviewResponseDto> data = reviewService.getReviewsByRevieweeId(
                userDetails.getUserId(),
                pageNo - 1
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("내가 받은 리뷰 조회에 성공했습니다.", data));
    }
}
