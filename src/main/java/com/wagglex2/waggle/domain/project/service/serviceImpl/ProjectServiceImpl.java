package com.wagglex2.waggle.domain.project.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.validator.PageableValidator;
import com.wagglex2.waggle.domain.application.dto.response.AppContentProjectResponseDto;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.request.ProjectSearchCondition;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectDetailResponseDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.repository.ProjectRepository;
import com.wagglex2.waggle.domain.project.service.ProjectService;
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
public class ProjectServiceImpl implements ProjectService {

    private static final Set<String> PROJECT_SORT_FIELDS = Set.of("createdAt");
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final BookmarkService bookmarkService;
    private final ApplicationService applicationService;
    private final PageableValidator pageableValidator;

    @Transactional
    @Override
    public Long createProject(Long userId, ProjectCreationRequestDto requestDto) {
        requestDto.validate();
        User user = userService.findById(userId);
        Project newProject = ProjectCreationRequestDto.toEntity(user, requestDto);

        Long projectId = projectRepository.save(newProject).getId();

        Team team = new Team(newProject);
        TeamMember leader = new TeamMember(team, user, TeamRole.LEADER);
        team.addMember(leader);
        teamService.save(team);

        return projectId;
    }

    @Transactional
    @Override
    public ProjectDetailResponseDto getProject(Long viewerId, Long projectId) {
        Project project = projectRepository.findWithAllById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // 삭제 여부 확인
        if (project.getStatus() == RecruitmentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }

        User viewer = userService.findById(viewerId);

        // 타 대학 공고를 조회하려는 경우
        if (viewer.getUniversity() != project.getUser().getUniversity()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_CROSS_UNIVERSITY_RECRUITMENT);
        }

        int updated = projectRepository.increaseViewCount(projectId);

        if (updated == 0) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }

        Optional<Long> bookmarkIdOptional =
                bookmarkService.findIdByUserIdAndRecruitmentId(viewerId, projectId);

        return ProjectDetailResponseDto.fromEntity(
                project,
                bookmarkIdOptional.isPresent(),
                bookmarkIdOptional.orElse(null)
        );
    }

    @Override
    public Page<ProjectSummaryResponseDto> getProjectSummaries(
            Long viewerId,
            ProjectSearchCondition condition,
            Pageable pageable
    ) {
        return projectRepository.getProjectSummaries(viewerId, condition, pageable);
    }

    /**
     * 주어진 Project ID 목록에 해당하는 프로젝트 요약 정보를 조회한다.
     * <p>
     * <ul>
     *      <li>컨트롤러 요청이 아닌 다른 서비스에서 호출하기 위한 메서드</li>
     *      <li>반환 리스트는 입력된 ID 순서를 보장</li>
     * </ul>
     *
     * @param projectIds 조회할 Project ID 목록
     * @return 입력 ID 순서에 맞춘 {@code List<ProjectSummaryResponseDto>}
     */
    @Override
    public List<ProjectSummaryResponseDto> getProjectSummariesByIds(Long viewerId, List<Long> projectIds) {
        return projectRepository.getProjectSummariesByIds(viewerId, projectIds);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Override
    public Page<RecruitmentWithAppsResponseDto> getAllByUserId(
            @P("userId") Long userId,
            Pageable pageable
    ) {
        pageableValidator.validate(pageable);
        pageableValidator.validateSort(pageable, PROJECT_SORT_FIELDS);

        Page<Project> projects = projectRepository.findAllByUserId(userId, pageable);

        // 공고 ID 목록
        List<Long> projectIds = projects.stream()
                .map(Project::getId)
                .toList();

        // 공고 ID로 application 전체 조회
        List<Application> applications = applicationService.findAllByRecruitmentIds(projectIds);

        // 공고ID -> applications 매핑 Map 생성
        Map<Long, List<Application>> appsByRecruitmentId = applications.stream()
                .collect(Collectors.groupingBy(
                        app -> app.getRecruitment().getId())
                );

        // DTO 조합
        List<RecruitmentWithAppsResponseDto> content = projects.stream()
                .map(p -> new RecruitmentWithAppsResponseDto(
                        p.getId(),
                        p.getTitle(),
                        p.getDeadline(),
                        appsByRecruitmentId.getOrDefault(
                                        p.getId(),
                                        List.of()
                                ).stream()
                                .map(a -> new AppContentProjectResponseDto(
                                        a.getId(),
                                        a.getApplicant().getId(),
                                        a.getApplicant().getNickname(),
                                        a.getMeetingType(),
                                        a.getGrade(),
                                        a.getContent(),
                                        a.getCreatedAt(),
                                        a.getPosition(),
                                        a.getSkills()
                                ))
                                .toList()
                ))
                .toList();

        return new PageImpl<>(content, pageable, projects.getTotalElements());
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Override
    public Page<ProjectSummaryResponseDto> getBookmarkedProjectsByUserId(Long userId, Pageable pageable) {
        // 찜한 프로젝트 공고 id 조회
        Page<Long> targetIds =
                bookmarkService.findBookmarkedRecruitmentIdsByUserId(
                        userId,
                        RecruitmentCategory.PROJECT,
                        pageable
                );

        // 조회할 공고가 없으면, 빈 리스트 반환
        if (targetIds.getContent().isEmpty()) {
            return new PageImpl<>(List.of(), pageable, targetIds.getTotalElements());
        }

        // targetIds에 해당하는 프로젝트 공고 정보 조회
        List<ProjectSummaryResponseDto> projectSummaries =
                getProjectSummariesByIds(userId, targetIds.getContent());

        return new PageImpl<>(projectSummaries, pageable, targetIds.getTotalElements());
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void updateProject(@P("userId") Long userId, Long projectId, ProjectUpdateRequestDto updateDto) {
        updateDto.validate();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // 권한 검증
        if (!userId.equals(project.getUser().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_UPDATE_ANOTHER_USER_PROJECT);
        }

        // 삭제 여부 검증
        if (project.getStatus() == RecruitmentStatus.CANCELED) {
            return;
        }

        project.update(updateDto);
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void deleteProject(@P("userId") Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROJECT_NOT_FOUND));

        // 이미 삭제 처리되었는지 확인
        if (project.getStatus() == RecruitmentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.PROJECT_NOT_FOUND);
        }

        // 권한 검증
        if (!userId.equals(project.getUser().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ANOTHER_USER_PROJECT);
        }

        // 팀 삭제 (팀 멤버도 같이 삭제)
        teamService.deleteByRecruitmentId(projectId);

        // 논리적 삭제
        project.cancel();
    }
}
