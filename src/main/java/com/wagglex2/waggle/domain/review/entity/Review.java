package com.wagglex2.waggle.domain.review.entity;

import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 리뷰(Review) 엔티티.
 *
 * <p><b>설명:</b></p>
 * <ul>
 *   <li>사용자 간 후기(리뷰) 정보를 저장하는 도메인 엔티티</li>
 *   <li>리뷰 작성자(reviewer)와 리뷰 대상(reviewee) 간의 다대일(ManyToOne) 관계를 가짐</li>
 *   <li>작성일(createdAt), 수정일(updatedAt)은 JPA Auditing으로 자동 관리됨</li>
 * </ul>
 *
 * <p><b>테이블 정보:</b></p>
 * <ul>
 *   <li>테이블명: {@code reviews}</li>
 *   <li>기본키: {@code id}</li>
 *   <li>작성자 외래키: {@code reviewer_id}</li>
 *   <li>대상자 외래키: {@code reviewee_id}</li>
 * </ul>
 */

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee;

    @Column(nullable = false, length = 100)
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Review(User reviewer, User reviewee, String content) {
        this.reviewer = reviewee;
        this.reviewee = reviewee;
        this.content = content;
    }
}
