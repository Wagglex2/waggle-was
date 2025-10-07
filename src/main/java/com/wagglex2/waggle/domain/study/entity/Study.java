package com.wagglex2.waggle.domain.study.entity;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.Period;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


/**
 * 스터디 모집 공고 엔티티.
 * <p>
 * BaseRecruitment를 상속하여 유저, 제목, 본문, 마감일을 포함하며
 * 스터디에 필요한 필드들을 추가로 정의한다.
 * </p>
 *
 * <ul>
 *   <li>{@link ParticipantInfo} : 모집 인원 및 현재 참여 인원</li>
 *   <li>{@link Period} : 스터디 기간</li>
 *   <li>{@link Skill} : 스터디 주제 기술 스택</li>
 * </ul>
 *
 * @author 박대형
 * @see BaseRecruitment
 */
@Table(name = "studies")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseRecruitment {
    @Embedded
    private ParticipantInfo participants;

    @Embedded
    private Period period;

    @Enumerated(value = EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recruitment_skills",
            joinColumns = @JoinColumn(name = "recruitment_id", referencedColumnName = "id")
    )
    private Set<Skill> skills = new HashSet<>();


    @Builder
    public Study(
            User user, String title, String content, LocalDateTime deadline,
            ParticipantInfo participants, Period period, Set<Skill> skills
    ) {
        super(user, RecruitmentCategory.STUDY, title, content, deadline);
        this.participants = participants;
        this.period = period;
        this.skills = skills;
    }
}
