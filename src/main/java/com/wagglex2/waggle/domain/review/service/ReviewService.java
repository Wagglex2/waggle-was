package com.wagglex2.waggle.domain.review.service;

import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import org.springframework.data.domain.Page;

public interface ReviewService {
    Long createReview(Long reviewerId, ReviewCreationRequestDto dto);
    Page<ReviewResponseDto> getReviewsByRevieweeId(Long revieweeId, int pageNo);
    Page<ReviewResponseDto> getReviewsByReviewerId(Long reviewerId, int pageNo);
}
