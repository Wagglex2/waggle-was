package com.wagglex2.waggle.domain.study.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.study.dto.request.StudyCreationRequestDto;
import com.wagglex2.waggle.domain.study.dto.response.StudyResponseDto;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.study.repository.StudyRepository;
import com.wagglex2.waggle.domain.study.service.StudyService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {
    private final StudyRepository studyRepository;
    private final UserService userService;

    @Transactional
    @Override
    public Long createStudy(StudyCreationRequestDto requestDto, Long userId) {
        User user = userService.findById(userId);
        Study newStudy = StudyCreationRequestDto.toEntity(user, requestDto);

        return studyRepository.save(newStudy).getId();
    }

    @Transactional
    @Override
    public StudyResponseDto getStudy(Long studyId) {
        int updated = studyRepository.increaseViewCount(studyId);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.STUDY_NOT_FOUND);
        }

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_NOT_FOUND));

        return StudyResponseDto.fromEntity(study);
    }
}
