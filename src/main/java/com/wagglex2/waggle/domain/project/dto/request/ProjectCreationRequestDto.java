package com.wagglex2.waggle.domain.project.dto.request;

import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PeriodRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PositionInfoCreationRequestDto;
import com.wagglex2.waggle.domain.common.type.PositionParticipantInfo;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 프로젝트 생성 요청 DTO
 * <p>
 * 프로젝트 생성 시 필요한 모든 필드를 포함한다.
 * {@link ProjectCommonRequestDto}를 상속하며, 생성된 DTO를 {@link Project} 엔티티로 변환할 수 있다.
 * </p>
 *
 * <p><b>보유 필드:</b>
 * <ul>
 *     <b>상위 클래스 필드</b>
 *     <li>{@link #title} - 공고 제목</li>
 *     <li>{@link #content} - 공고 본문</li>
 *     <li>{@link #deadline} - 마감일</li>
 *     <li>{@link #purpose} - 프로젝트 목적</li>
 *     <li>{@link #meetingType} - 진행 방식</li>
 *     <li>{@link #skills} - 기술 스택</li>
 *     <li>{@link #grades} - 모집 학년</li>
 *     <li>{@link #period} - 프로젝트 기간</li>
 *     <br>
 *     <b>현재 클래스 필드</b>
 *     <li>{@link #positions} - 프로젝트 포지션 정보</li>
 * </ul>
 *
 * <p><b>상속 관계:</b>
 * <ul>
 *     <li>상위 클래스: {@link ProjectCommonRequestDto}</li>
 * </ul>
 */
@Getter
public class ProjectCreationRequestDto extends ProjectCommonRequestDto {
    @NotEmpty(message = "포지션 정보가 누락되었습니다.")
    @Valid
    private final Set<PositionInfoCreationRequestDto> positions;

    public ProjectCreationRequestDto(
            String title, String content, LocalDateTime deadline,
            ProjectPurpose purpose, MeetingType meetingType, Set<Skill> skills,
            Set<GradeRequestDto> grades, PeriodRequestDto period,
            Set<PositionInfoCreationRequestDto> positions
    ) {
        super(title, content, deadline, purpose, meetingType, skills, grades, period);
        this.positions = positions;
    }

    public static Project toEntity(User user, ProjectCreationRequestDto dto) {
        Set<Integer> grades = dto.getGrades().stream()
                .map(GradeRequestDto::grade)
                .collect(Collectors.toSet());

        Set<PositionParticipantInfo> positions = dto.getPositions().stream()
                .map(PositionInfoCreationRequestDto::to)
                .collect(Collectors.toSet());

        return Project.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .deadline(dto.getDeadline())
                .purpose(dto.getPurpose())
                .meetingType(dto.getMeetingType())
                .positions(positions)
                .skills(dto.getSkills())
                .grades(grades)
                .period(PeriodRequestDto.to(dto.getPeriod()))
                .build();
    }
}
