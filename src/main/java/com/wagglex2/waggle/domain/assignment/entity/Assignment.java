package com.wagglex2.waggle.domain.assignment.entity;

import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentUpdateRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.GradeRequestDto;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 과제 모집 공고 엔티티.
 * <p>
 * BaseRecruitment를 상속하여 유저, 제목, 본문, 마감일 등)을 포함하며
 * 과제 공고에 필요한 추가 필드를 정의한다.
 * </p>
 *
 * <ul>
 *   <li>department : 개설 학과명</li>
 *   <li>lecture : 과목명</li>
 *   <li>lectureCode : 과목 코드</li>
 * </ul>
 *
 * @author 박대형
 * @see BaseRecruitment
 */
@Table(name = "assignments")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Assignment extends BaseRecruitment {
    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String lecture;

    @Column(nullable = false)
    private String lectureCode;

    @Embedded
    private ParticipantInfo participants;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recruitment_grades",
            joinColumns = @JoinColumn(name = "recruitment_id", referencedColumnName = "id")
    )
    private Set<Integer> grades = new HashSet<>();

    @Builder
    public Assignment(
            User user, String title, String content, LocalDateTime deadline,
            String department, String lecture, String lectureCode,
            ParticipantInfo participants, Set<Integer> grades
    ) {
        super(user, RecruitmentCategory.ASSIGNMENT, title, content, deadline);
        this.department = department;
        this.lecture = lecture;
        this.lectureCode = lectureCode;
        this.participants = participants;
        this.grades = grades;
    }

    public void update(AssignmentUpdateRequestDto dto) {
        update(dto.getTitle(), dto.getContent(), dto.getDeadline());

        Set<Integer> grades = dto.getGrades().stream()
                .map(GradeRequestDto::grade)
                .collect(Collectors.toSet());

        this.department = dto.getDepartment();
        this.lecture = dto.getLecture();
        this.lectureCode = dto.getLectureCode();
        this.participants = new ParticipantInfo(dto.getMaxParticipants());
        this.grades.clear();
        this.grades.addAll(grades);
        changeStatusByDeadline();
    }
}
