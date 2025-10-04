package com.wagglex2.waggle.domain.review.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.entity.Review;
import com.wagglex2.waggle.domain.review.repository.ReviewRepository;
import com.wagglex2.waggle.domain.review.service.ReviewService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;

    /**
     * 리뷰를 생성하는 서비스 로직.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>작성자(authorId)가 자기 자신에게 리뷰를 남기려는 경우 예외 발생</li>
     *   <li>작성자(User)와 리뷰 대상(User) 엔티티 조회</li>
     *   <li>리뷰 생성 요청 DTO를 기반으로 Review 엔티티 생성</li>
     *   <li>생성된 Review 엔티티를 저장 후 식별자(ID) 반환</li>
     * </ol>
     *
     * @param authorId 리뷰 작성자 ID
     * @param dto 리뷰 생성 요청 DTO (리뷰 대상 사용자 ID, 내용)
     * @return 생성된 리뷰의 ID
     * @throws BusinessException 자기 자신에게 리뷰를 남기려는 경우 발생
     */
    @Override
    @Transactional
    public Long createReview(Long authorId, ReviewCreationRequestDto dto) {

        if (authorId.equals(dto.userTargetId())) {
            throw new BusinessException(ErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }

        User author = userService.findById(authorId);
        User target = userService.findById(dto.userTargetId());

        Review review = dto.toEntity(author, target, dto.content());
        return reviewRepository.save(review).getId();
    }
}
