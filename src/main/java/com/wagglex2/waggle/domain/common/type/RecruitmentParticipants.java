package com.wagglex2.waggle.domain.common.type;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Embeddable
public class RecruitmentParticipants {
    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @Column(name = "curr_participants", nullable = false)
    private int currParticipants = 0;

    public RecruitmentParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}
