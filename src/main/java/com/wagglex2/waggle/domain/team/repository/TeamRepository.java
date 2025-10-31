package com.wagglex2.waggle.domain.team.repository;

import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.team.entity.Team;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * 특정 사용자가 생성한 모집공고(프로젝트/스터디/과제)에 속한 팀 목록을
     * 카테고리(category) 및 상태(status) 기준으로 페이징 조회하는 쿼리.
     *
     * <p>
     * <ul>
     *   <li>Team 엔티티를 기준으로, 연관된 모집공고(BaseRecruitment)와 작성자(User)를 함께 조회한다.</li>
     *   <li>카테고리(RecruitmentCategory)와 모집 상태(RecruitmentStatus)로 필터링한다.</li>
     *   <li>N+1 문제를 방지하기 위해 EntityGraph를 사용하여 연관 엔티티를 즉시 로딩한다.</li>
     * </ul>
     * </p>
     *
     * @param userId   모집공고 작성자(User)의 ID
     * @param category 모집 카테고리 (PROJECT, STUDY, ASSIGNMENT 등)
     * @param status   모집 상태 (RECRUITING, CLOSED, CANCELED 등)
     * @param pageable 페이지 번호, 크기, 정렬 기준 정보를 포함한 Pageable 객체
     * @return         Page 형태로 감싼 Team 엔티티 목록
     */
    @EntityGraph(
            attributePaths = {
                    "recruitment",
                    "recruitment.user",
            },
            type = EntityGraph.EntityGraphType.LOAD
    )
    @Query(
            value = """
                    SELECT DISTINCT t
                    FROM Team t
                    WHERE t.recruitment.category = :category
                    AND t.recruitment.status = :status
                    AND t.recruitment.user.id = :userId
                    """,
            countQuery = """
                    SELECT COUNT(t)
                    FROM Team t
                    WHERE t.recruitment.category = :category
                    AND t.recruitment.status = :status
                    AND t.recruitment.user.id = :userId
                    """)
    Page<Team> findByUserIdAndCategoryAndStatus(
            @Param("userId") Long userId,
            @Param("category") RecruitmentCategory category,
            @Param("status") RecruitmentStatus status,
            Pageable pageable
    );
}
