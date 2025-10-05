package com.wagglex2.waggle.domain.review.service;

import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;

public interface ReviewService {
    Long createReview(Long reviewerId, ReviewCreationRequestDto dto);
}
