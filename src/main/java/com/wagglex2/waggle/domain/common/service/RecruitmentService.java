package com.wagglex2.waggle.domain.common.service;

public interface RecruitmentService {

    /**
     * 마감일이 지난 모집 공고의 상태를 CLOSED로 변경
     */
    void closeExpiredRecruitments();
}
