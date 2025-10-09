package com.wagglex2.waggle.domain.review.service;

import com.wagglex2.waggle.domain.common.dto.response.PageResponse;
import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Long createReview(Long reviewerId, ReviewCreationRequestDto dto);
    PageResponse<ReviewResponseDto> getReviewsByRevieweeId(Long revieweeId, Pageable pageable);
    PageResponse<ReviewResponseDto> getReviewsByReviewerId(Long reviewerId, Pageable pageable);
}
