package com.wagglex2.waggle.domain.project.type;

import com.wagglex2.waggle.domain.common.type.PositionType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Embeddable
public class Position {
    @Column(name = "position", nullable = false, length = 16)
    private final PositionType type;

    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @Column(name = "curr_participants", nullable = false)
    private int currParticipants = 0;

    public Position(PositionType type, int maxParticipants) {
        this.type = type;
        this.maxParticipants = maxParticipants;
    }
}
