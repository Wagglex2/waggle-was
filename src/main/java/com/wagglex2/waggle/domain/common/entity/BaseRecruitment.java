package com.wagglex2.waggle.domain.common.entity;

import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 공고(Entity)의 부모 클래스.
 * <p>
 * 공통적으로 사용되는 필드를 정의하고, 이를 상속받아 각 도메인별 모집 공고를 확장한다.
 * </p>
 *
 * <ul>
 *   <li>{@link User} : 공고 작성자 (N:1 관계)</li>
 *   <li>{@link RecruitmentCategory} : 공고 카테고리</li>
 *   <li>title : 공고 제목</li>
 *   <li>content : 공고 본문 (최대 4096자)</li>
 *   <li>deadline : 모집 마감일</li>
 *   <li>{@link RecruitmentStatus} : 모집 상태 (기본값: RECRUITING)</li>
 *   <li>createdAt : 공고 생성 시각 (JPA Auditing)</li>
 *   <li>viewCount : 조회수 (기본값: 0)</li>
 * </ul>
 *
 * @see Project
 * @author 오재민
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseRecruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecruitmentCategory category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4096)
    private String content;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecruitmentStatus status = RecruitmentStatus.RECRUITING;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    protected BaseRecruitment(
            User user, RecruitmentCategory category,
            String title, String content, LocalDateTime deadline
    ) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
        this.deadline = deadline;
    }
}
