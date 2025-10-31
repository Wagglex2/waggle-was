package com.wagglex2.waggle.domain.team.entity;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Team 엔티티
 *
 * <p>각 공고(BaseRecruitment)가 생성될 때 1:1로 연결되는 팀 정보를 관리한다.</p>
 *
 * <ul>
 *   <li>하나의 팀은 하나의 모집 공고(BaseRecruitment)와 반드시 연결된다. (1:1 관계)</li>
 *   <li>하나의 팀은 여러 명의 팀원(TeamMember)을 가진다. (1:N 관계)</li>
 * </ul>
 *
 * <p><b>연관관계 관리:</b></p>
 * <ul>
 *   <li>팀 삭제 시 팀원(TeamMember)도 함께 삭제된다. (cascade = ALL, orphanRemoval = true)</li>
 *   <li>팀원 추가 시 {@link #addMember(TeamMember)} 메서드를 통해 양방향 관계를 일관성 있게 유지한다.</li>
 * </ul>
 *
 * @author 김민재
 */
@Entity
@Table(name = "teams")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false, unique = true)
    private BaseRecruitment recruitment;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    @OrderBy("role ASC") // LEADER 우선
    private List<TeamMember> members = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Team(BaseRecruitment recruitment) {
        this.recruitment = recruitment;
    }

    public void addMember(TeamMember member) {
        this.members.add(member);
        member.setTeam(this);
    }
}
