package com.wagglex2.waggle.domain.project.type;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.RecruitmentParticipants;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Embeddable
public class ProjectParticipants {
    @Column(name = "position", nullable = false, length = 16)
    private final PositionType type;

    @Embedded
    private RecruitmentParticipants participants;

    public ProjectParticipants(PositionType type, RecruitmentParticipants participants) {
        this.type = type;
        this.participants = participants;
    }
}
