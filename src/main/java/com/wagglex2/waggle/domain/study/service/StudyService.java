package com.wagglex2.waggle.domain.study.service;

import com.wagglex2.waggle.domain.study.dto.request.StudyCreationRequestDto;

public interface StudyService {
    Long createStudy(StudyCreationRequestDto studyCreationRequestDto, Long userId);
}
