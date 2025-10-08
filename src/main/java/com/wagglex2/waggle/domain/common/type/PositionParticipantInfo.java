package com.wagglex2.waggle.domain.common.type;

import com.wagglex2.waggle.domain.common.dto.request.PositionInfoCreationRequestDto;
import com.wagglex2.waggle.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;

/**
 * 포지션 별 모집 인원 현황을 나타내는 VO 클래스
 * <p>
 * <ul>
 *   <li>position: 포지션</li>
 *   <li>maxParticipants: 최대 모집 인원</li>
 *   <li>currParticipants: 현재 모집된 인원</li>
 * </ul>
 * <p>
 * {@link PositionType} 정보가 필요한 {@link Project}에서 사용된다.
 *
 * @see ParticipantInfo
 * @see PositionInfoCreationRequestDto
 * @see Project
 * @author 오재민
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EqualsAndHashCode
@Getter
@Embeddable
public class PositionParticipantInfo {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private final PositionType position;

    @Embedded
    private ParticipantInfo participantInfo;
}
