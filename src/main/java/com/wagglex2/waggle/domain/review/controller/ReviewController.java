package com.wagglex2.waggle.domain.review.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.dto.response.PageResponse;
import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import com.wagglex2.waggle.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
     * 로그인한 사용자가 작성한 리뷰 목록을 페이지네이션 방식으로 조회한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>Spring MVC가 요청 파라미터({@code page}, {@code size}, {@code sort})를 자동으로 {@link Pageable} 객체로 변환한다.</li>
     *   <li>인증된 사용자 정보에서 작성자 ID({@code reviewerId})를 추출한다.</li>
     *   <li>{@code reviewService.getReviewsByReviewerId()} 호출 시 {@link Pageable}을 그대로 전달한다.</li>
     *   <li>조회 결과({@link Page}<{@link ReviewResponseDto}>)를 {@link PageResponse}로 변환한다.</li>
     *   <li>최종적으로 {@link ApiResponse} 형태로 감싸 200 OK 응답을 반환한다.</li>
     * </ol>
     *
     * <p><b>요청 파라미터 예시:</b></p>
     * <ul>
     *   <li>{@code GET /me/written?page=0&size=5&sort=createdAt,desc}</li>
     *   <li>페이지 번호는 0부터 시작 (Spring Data JPA의 기본 규칙)</li>
     * </ul>
     *
     * @param userDetails 인증된 사용자 정보
     * @param pageable    페이지 정보 (기본값: size=5, sort=createdAt, direction=DESC)
     * @return 리뷰 목록을 포함한 200 OK 응답
     */
    @GetMapping("/me/written")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponseDto>>> getMyWrittenReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<ReviewResponseDto> page = reviewService.getReviewsByReviewerId(
                userDetails.getUserId(),
                pageable
        );

        PageResponse<ReviewResponseDto> data = PageResponse.from(page);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("내가 작성한 리뷰 조회에 성공했습니다.", data));
    }

    /**
     * 로그인한 사용자가 <b>받은 리뷰 목록</b>을 페이지네이션 방식으로 조회한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>Spring MVC가 요청 파라미터({@code page}, {@code size}, {@code sort})를 자동으로 {@link Pageable} 객체로 변환한다.</li>
     *   <li>인증 정보에서 리뷰 대상 사용자 ID({@code revieweeId})를 추출한다.</li>
     *   <li>{@code reviewService.getReviewsByRevieweeId()} 호출 시 {@link Pageable}을 그대로 전달한다.</li>
     *   <li>조회 결과({@link Page}<{@link ReviewResponseDto}>)를 {@link PageResponse}로 변환한다.</li>
     *   <li>최종적으로 {@link ApiResponse} 형태로 감싸 200 OK 응답을 반환한다.</li>
     * </ol>
     *
     * <p><b>요청 파라미터 예시:</b></p>
     * <ul>
     *   <li>{@code GET /me/received?page=0&size=5&sort=createdAt,desc}</li>
     *   <li>페이지 번호는 0부터 시작 (Spring Data JPA 기본 규칙)</li>
     * </ul>
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param pageable    페이지 정보 (기본값: size=5, sort=createdAt, direction=DESC)
     * @return 받은 리뷰 목록을 포함한 200 OK 응답
     */
    @GetMapping("/me/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponseDto>>> getMyReceivedReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<ReviewResponseDto> page = reviewService.getReviewsByRevieweeId(
                userDetails.getUserId(),
                pageable
        );

        PageResponse<ReviewResponseDto> data = PageResponse.from(page);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("내가 받은 리뷰 조회에 성공했습니다.", data));
    }
}
