package com.wagglex2.waggle.domain.review.dto.response;

import com.wagglex2.waggle.domain.review.entity.Review;

/**
 * 리뷰 응답 DTO.
 *
 * <p><b>설명:</b></p>
 * <ul>
 *   <li>리뷰(Review) 엔티티를 클라이언트에 반환하기 위한 데이터 전송 객체 (DTO)</li>
 *   <li>현재는 리뷰 내용(content)만 포함하지만, 추후 작성자·작성일 등으로 확장 가능</li>
 *   <li>{@code static from()} 정적 팩토리 메서드를 통해 엔티티를 DTO로 변환</li>
 * </ul>
 *
 * @see com.wagglex2.waggle.domain.review.entity.Review
 */
public record ReviewResponseDto(
        String content
) {
    public static ReviewResponseDto from(Review review) {
        return new ReviewResponseDto(review.getContent());
    }
}
