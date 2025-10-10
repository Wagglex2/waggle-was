package com.wagglex2.waggle.domain.project.repository;

import com.wagglex2.waggle.domain.project.dto.request.ProjectSearchCondition;
import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * QueryDSL을 활용한 Project 커스텀 리포지토리
 */
public interface ProjectRepositoryCustom {

    /**
     * 검색 조건({@link ProjectSearchCondition})에 맞는 프로젝트 요약 DTO를 페이징 조회
     * @param condition 검색 조건 ({@link ProjectSearchCondition})
     * @param pageable  페이징 및 정렬 정보 ({@link org.springframework.data.domain.Pageable})
     * @return 조건에 맞는 프로젝트 요약 DTO 페이지 ({@link org.springframework.data.domain.Page}&lt;{@link ProjectSummaryResponseDto}&gt;)
     */
    Page<ProjectSummaryResponseDto> findProjectSummaries(ProjectSearchCondition condition, Pageable pageable);
}
