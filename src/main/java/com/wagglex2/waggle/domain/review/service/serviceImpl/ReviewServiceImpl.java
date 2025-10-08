package com.wagglex2.waggle.domain.review.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import com.wagglex2.waggle.domain.review.entity.Review;
import com.wagglex2.waggle.domain.review.repository.ReviewRepository;
import com.wagglex2.waggle.domain.review.service.ReviewService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final static int DEFAULT_PAGE_SIZE = 5;

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
     * @param reviewerId 리뷰 작성자 ID
     * @param dto        리뷰 생성 요청 DTO (리뷰 대상 사용자 ID, 내용)
     * @return 생성된 리뷰의 ID
     * @throws BusinessException 자기 자신에게 리뷰를 남기려는 경우 발생
     */
    @Override
    @Transactional
    public Long createReview(Long reviewerId, ReviewCreationRequestDto dto) {

        if (reviewerId.equals(dto.revieweeId())) {
            throw new BusinessException(ErrorCode.SELF_REVIEW_NOT_ALLOWED);
        }

        User reviewer = userService.findById(reviewerId);
        User reviewee = userService.findById(dto.revieweeId());

        Review review = dto.toEntity(reviewer, reviewee, dto.content());
        return reviewRepository.save(review).getId();
    }

    /**
     * 특정 사용자가 받은 리뷰 목록을 {@link Pageable} 조건에 따라 페이지네이션 조회한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>컨트롤러에서 전달받은 {@link Pageable} 객체를 기반으로 페이징 및 정렬 조건을 설정</li>
     *   <li>{@code revieweeId}에 해당하는 리뷰를 {@link ReviewRepository#findByRevieweeId(Long, Pageable)}로 조회</li>
     *   <li>조회된 {@link Review} 엔티티를 {@link ReviewResponseDto}로 변환하여 반환</li>
     * </ol>
     *
     * @param revieweeId 리뷰 대상 사용자의 고유 ID
     * @param pageable   페이징 및 정렬 정보
     * @return {@link ReviewResponseDto}를 요소로 가지는 {@link Page} 객체
     */
    @Override
    public Page<ReviewResponseDto> getReviewsByRevieweeId(Long revieweeId, Pageable pageable) {
        return reviewRepository.findByRevieweeId(revieweeId, pageable)
                .map(ReviewResponseDto::from);
    }

    /**
     * 특정 사용자가 <b>작성한 리뷰 목록</b>을 {@link Pageable} 조건에 따라 페이지네이션 조회한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>컨트롤러로부터 전달받은 {@link Pageable} 객체를 통해 페이징 및 정렬 조건을 설정</li>
     *   <li>{@code reviewerId}에 해당하는 리뷰를 {@link ReviewRepository#findByReviewerId(Long, Pageable)}로 조회</li>
     *   <li>조회된 {@link Review} 엔티티를 {@link ReviewResponseDto}로 변환하여 반환</li>
     * </ol>
     *
     * @param reviewerId 리뷰 작성 사용자의 고유 ID
     * @param pageable   페이징 및 정렬 정보
     * @return {@link ReviewResponseDto}를 요소로 가지는 {@link Page} 객체
     */
    @Override
    public Page<ReviewResponseDto> getReviewsByReviewerId(Long reviewerId, Pageable pageable) {
        return reviewRepository.findByReviewerId(reviewerId, pageable)
                .map(ReviewResponseDto::from);
    }
}
