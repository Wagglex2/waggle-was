package com.wagglex2.waggle.domain.assignment.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.validator.PageableValidator;
import com.wagglex2.waggle.domain.application.dto.response.AppContentSimpleResponseDto;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentSearchCondition;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentUpdateRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentDetailResponseDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentSummaryResponseDto;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.assignment.repository.AssignmentRepository;
import com.wagglex2.waggle.domain.assignment.service.AssignmentService;
import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.team.entity.Team;
import com.wagglex2.waggle.domain.team.service.TeamService;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;
import com.wagglex2.waggle.domain.team_member.entity.type.TeamRole;
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
public class AssignmentServiceImpl implements AssignmentService {
    private static final Set<String> ASSIGNMENT_SORT_FIELDS = Set.of("createdAt");
    private final AssignmentRepository assignmentRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final BookmarkService bookmarkService;
    private final ApplicationService applicationService;
    private final PageableValidator pageableValidator;

    @Transactional
    @Override
    public Long createAssignment(AssignmentCreationRequestDto requestDto, Long userId) {
        User user = userService.findById(userId);
        Assignment newAssignment = AssignmentCreationRequestDto.toEntity(user, requestDto);

        Long assignmentId = assignmentRepository.save(newAssignment).getId();

        Team team = new Team(newAssignment);
        TeamMember leader = new TeamMember(team, user, TeamRole.LEADER);
        team.addMember(leader);
        teamService.save(team);

        return assignmentId;
    }

    @Transactional
    @Override
    public AssignmentDetailResponseDto getAssignment(Long viewerId, Long assignmentId) {
        Assignment assignment = assignmentRepository.findWithAllById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 삭제 여부 확인
        if (assignment.getStatus() == RecruitmentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }

        User viewer = userService.findById(viewerId);

        // 타 대학 공고를 조회하려는 경우
        if (viewer.getUniversity() != assignment.getUser().getUniversity()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_CROSS_UNIVERSITY_RECRUITMENT);
        }

        int updated = assignmentRepository.increaseViewCount(assignmentId);

        if (updated == 0) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }

        Optional<Long> bookmarkIdOptional =
                bookmarkService.findIdByUserIdAndRecruitmentId(viewerId, assignmentId);

        return AssignmentDetailResponseDto.fromEntity(
                assignment,
                bookmarkIdOptional.isPresent(),
                bookmarkIdOptional.orElse(null)
        );
    }

    @Override
    public Page<AssignmentSummaryResponseDto> getAssignmentSummaries(
            Long viewerId,
            AssignmentSearchCondition condition,
            Pageable pageable
    ) {
        return assignmentRepository.getAssignmentSummaries(viewerId, condition, pageable);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Override
    public Page<RecruitmentWithAppsResponseDto> getAllByUserId(
            @P("userId") Long userId,
            Pageable pageable
    ) {
        pageableValidator.validate(pageable);
        pageableValidator.validateSort(pageable, ASSIGNMENT_SORT_FIELDS);

        Page<Assignment> assignments = assignmentRepository.findAllByUserId(userId, pageable);

        // 공고 ID 목록
        List<Long> assignmnetIds = assignments.stream()
                .map(Assignment::getId)
                .toList();

        // 공고 ID로 application 전체 조회
        List<Application> applications = applicationService.findAllByRecruitmentIds(assignmnetIds);

        // 공고 ID -> applications 매핑 Map 생성
        Map<Long, List<Application>> appsByRecruitmentId = applications.stream()
                .collect(Collectors.groupingBy(
                        app -> app.getRecruitment().getId())
                );

        // DTO 조합
        List<RecruitmentWithAppsResponseDto> content = assignments.stream()
                .map(a -> new RecruitmentWithAppsResponseDto(
                        a.getId(),
                        a.getTitle(),
                        a.getDeadline(),
                        appsByRecruitmentId.getOrDefault(
                                        a.getId(),
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

        return new PageImpl<>(content, pageable, assignments.getTotalElements());
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void updateAssignment(
            @P("userId") Long userId, Long assignmentId, AssignmentUpdateRequestDto updateDto
    ) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        if (!userId.equals(assignment.getUser().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (assignment.getStatus() == RecruitmentStatus.CANCELED) {
            return;
        }

        assignment.update(updateDto);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void deleteAssignment(@P("userId") Long userId, Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 이미 삭제 처리되었는지 확인
        if (assignment.getStatus() == RecruitmentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }

        // 권한 검증
        if (!userId.equals(assignment.getUser().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ANOTHER_USER_ASSIGNMENT);
        }

        // 팀 삭제 (팀 멤버도 같이 삭제)
        teamService.deleteByRecruitmentId(assignmentId);

        // 논리적 삭제
        assignment.cancel();
    }
}
