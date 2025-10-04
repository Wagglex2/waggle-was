package com.wagglex2.waggle.domain.review.dto.request;

import com.wagglex2.waggle.domain.review.entity.Review;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 리뷰 생성 요청을 전달받는 DTO.
 *
 * <p><b>설명:</b></p>
 * <ul>
 *   <li>리뷰 작성자가 특정 사용자에게 후기를 남길 때 사용하는 요청 데이터 구조</li>
 *   <li>검증 애노테이션(@NotNull, @NotBlank, @Size)을 통해 필수 값 유효성 검사 수행</li>
 *   <li>엔티티 변환 메서드 {@link #toEntity(User, User, String)} 를 통해 Review 객체로 변환 가능</li>
 * </ul>
 *
 * <p><b>검증 규칙:</b></p>
 * <ul>
 *   <li>{@code revieweeId} – 대상 사용자 ID는 반드시 존재해야 함</li>
 *   <li>{@code content} – 후기 내용은 비어 있을 수 없으며 100자 이내로 제한</li>
 * </ul>
 *
 * @param revieweeId 후기 대상 사용자 ID
 * @param content 후기 내용
 */
public record ReviewCreationRequestDto(
        @NotNull(message = "대상 유저 ID가 누락되었습니다.")
        Long revieweeId,

        @NotBlank(message = "후기 내용이 누락되었습니다.")
        @Size(max = 100, message = "내용은 100자 이내로 입력해주세요.")
        String content
) {
    public Review toEntity(User reviewer, User reviewee, String content) {
        return Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .content(content)
                .build();
    }
}
