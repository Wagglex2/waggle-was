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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
     * 특정 사용자가 받은 리뷰 목록을 페이지네이션 방식으로 조회한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>pageNo(요청 페이지 번호), DEFAULT_PAGE_SIZE(고정 페이지 크기), createdAt 기준 내림차순 정렬을 설정</li>
     *   <li>revieweeId(리뷰 대상 사용자 ID)에 해당하는 리뷰를 Page 단위로 조회</li>
     *   <li>조회된 Review 엔티티를 ReviewResponseDto로 변환하여 반환</li>
     * </ol>
     *
     * @param revieweeId 리뷰 대상 사용자의 ID
     * @param pageNo     조회할 페이지 번호 (0부터 시작)
     * @return 리뷰 목록이 담긴 Page 객체 (ReviewResponseDto 형태)
     */
    @Override
    public Page<ReviewResponseDto> getReviews(Long revieweeId, int pageNo) {
        Pageable pageable = PageRequest.of(
                pageNo,
                DEFAULT_PAGE_SIZE,
                Sort.by("createdAt").descending()
        );

        return reviewRepository.findByRevieweeId(revieweeId, pageable)
                .map(ReviewResponseDto::from);
    }
}
