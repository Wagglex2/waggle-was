package com.wagglex2.waggle.domain.study.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.validator.PageableValidator;
import com.wagglex2.waggle.domain.application.dto.response.AppContentSimpleResponseDto;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.study.dto.request.StudyCreationRequestDto;
import com.wagglex2.waggle.domain.study.dto.request.StudyUpdateRequestDto;
import com.wagglex2.waggle.domain.study.dto.response.StudyResponseDto;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.study.repository.StudyRepository;
import com.wagglex2.waggle.domain.study.service.StudyService;
import com.wagglex2.waggle.domain.team.service.TeamService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {
    private static final Set<String> STUDY_SORT_FIELDS = Set.of("createdAt");
    private final StudyRepository studyRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final BookmarkService bookmarkService;
    private final ApplicationService applicationService;
    private final PageableValidator pageableValidator;

    @Transactional
    @Override
    public Long createStudy(StudyCreationRequestDto requestDto, Long userId) {
        User user = userService.findById(userId);
        Study newStudy = StudyCreationRequestDto.toEntity(user, requestDto);

        return studyRepository.save(newStudy).getId();
    }

    @Transactional
    @Override
    public StudyResponseDto getStudy(Long viewerId, Long studyId) {
        Study study = studyRepository.findWithAllById(studyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_NOT_FOUND));

        // 삭제 여부 확인
        if (study.getStatus() == RecruitmentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.STUDY_NOT_FOUND);
        }

        User viewer = userService.findById(viewerId);

        // 타 대학 공고를 조회하려는 경우
        if (viewer.getUniversity() != study.getUser().getUniversity()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_CROSS_UNIVERSITY_RECRUITMENT);
        }

        int updated = studyRepository.increaseViewCount(studyId);

        if (updated == 0) {
            throw new BusinessException(ErrorCode.STUDY_NOT_FOUND);
        }

        Optional<Long> bookmarkIdOptional =
                bookmarkService.findIdByUserIdAndRecruitmentId(viewerId, studyId);

        return StudyResponseDto.fromEntity(
                study,
                bookmarkIdOptional.isPresent(),
                bookmarkIdOptional.orElse(null)
        );
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Override
    public Page<RecruitmentWithAppsResponseDto> getAllByUserId(
            @P("userId") Long userId,
            Pageable pageable
    ) {
        pageableValidator.validate(pageable);
        pageableValidator.validateSort(pageable, STUDY_SORT_FIELDS);

        Page<Study> studies = studyRepository.findAllByUserId(userId, pageable);

        // 공고 ID 목록
        List<Long> assignmnetIds = studies.stream()
                .map(Study::getId)
                .toList();

        // 공고 ID로 application 전체 조회
        List<Application> applications = applicationService.findAllByRecruitmentIds(assignmnetIds);

        // 공고 ID -> applications 매핑 Map 생성
        Map<Long, List<Application>> appsByRecruitmentId = applications.stream()
                .collect(Collectors.groupingBy(
                        app -> app.getRecruitment().getId())
                );

        // DTO 조합
        List<RecruitmentWithAppsResponseDto> content = studies.stream()
                .map(s -> new RecruitmentWithAppsResponseDto(
                        s.getId(),
                        s.getTitle(),
                        s.getDeadline(),
                        appsByRecruitmentId.getOrDefault(
                                        s.getId(),
                                        List.of()
                                ).stream()
                                .map(app -> new AppContentSimpleResponseDto(
                                        app.getId(),
                                        app.getApplicant().getId(),
                                        app.getApplicant().getNickname(),
                                        app.getMeetingType(),
                                        app.getGrade(),
                                        app.getContent(),
                                        app.getCreatedAt()
                                ))
                                .toList()
                ))
                .toList();

        return new PageImpl<>(content, pageable, studies.getTotalElements());
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void updateStudy(
            @P("userId") Long userId, Long studyId, StudyUpdateRequestDto updateDto
    ) {
        updateDto.validate();
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_NOT_FOUND));

        if (!userId.equals(study.getUser().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (study.getStatus() == RecruitmentStatus.CANCELED) {
            return;
        }

        study.update(updateDto);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void deleteStudy(@P("userId") Long userId, Long studyId) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_NOT_FOUND));

        if (study.getStatus() == RecruitmentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.STUDY_NOT_FOUND);
        }
        
        // 권한 검증
        if (!userId.equals(study.getUser().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 팀 삭제 (팀 멤버도 같이 삭제)
        teamService.deleteByRecruitmentId(studyId);

        // 논리적 삭제
        study.cancel();
    }
}
