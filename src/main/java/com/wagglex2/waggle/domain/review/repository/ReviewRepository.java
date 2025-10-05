package com.wagglex2.waggle.domain.review.repository;

import com.wagglex2.waggle.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByRevieweeId(Long revieweeId, Pageable pageable);
    Page<Review> findByReviewerId(Long reviewerId, Pageable pageable);
}
