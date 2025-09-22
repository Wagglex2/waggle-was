package com.wagglex2.waggle.domain.project.entity;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.project.type.Position;
import com.wagglex2.waggle.domain.project.type.ProjectPurpose;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 프로젝트 모집 공고 엔티티.
 * <p>
 * BaseRecruitment를 상속하여 유저, 제목, 본문, 마감일을 포함하며
 * 프로젝트 공고에 필요한 추가 필드를 정의한다.
 * </p>
 *
 * <ul>
 *   <li>{@link ProjectPurpose} : 프로젝트 목적</li>
 *   <li>{@link MeetingType} : 모임 방식 (온/오프라인)</li>
 *   <li>{@link Position} : 모집 포지션 리스트 (ElementCollection)</li>
 *   <li>{@link Skill} : 요구 기술 스택 (ElementCollection)</li>
 *   <li>grades : 지원 가능 학년 (ElementCollection)</li>
 *   <li>startDate, endDate : 프로젝트 기간</li>
 * </ul>
 *
 * @author 오재민
 * @see BaseRecruitment
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseRecruitment {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectPurpose purpose;

    @Column(name = "meeting_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recruitment_positions",
            joinColumns = @JoinColumn(name = "recruitment_id", referencedColumnName = "id")
    )
    private List<Position> positions = new ArrayList<>();

    @Column(name = "skill")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recruitment_skills",
            joinColumns = @JoinColumn(name = "recruitment_id", referencedColumnName = "id")
    )
    private List<Skill> skills = new ArrayList<>();

    @Column(name = "grade")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recruitment_grades",
            joinColumns = @JoinColumn(name = "recruitment_id", referencedColumnName = "id")
    )
    private List<Integer> grades = new ArrayList<>();

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder
    public Project(
            User user, String title, String content, LocalDateTime deadline,
            ProjectPurpose purpose, MeetingType meetingType,
            List<Position> positions, List<Skill> skills, List<Integer> grades,
            LocalDate startDate, LocalDate endDate
    ) {
        super(user, RecruitmentCategory.PROJECT, title, content, deadline);
        this.purpose = purpose;
        this.meetingType = meetingType;
        this.positions = positions;
        this.skills = skills;
        this.grades = grades;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
