package com.wagglex2.waggle.domain.user.entity;

import com.wagglex2.waggle.domain.common.type.PositionType;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.user.entity.type.University;
import com.wagglex2.waggle.domain.user.entity.type.UserRoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * User 엔티티
 *
 * <p>와글와글에 가입한 사용자의 기본 정보를 관리한다.</p>
 *
 * <ul>
 *     <li>username : 로그인 ID (unique)</li>
 *     <li>password : 로그인 비밀번호 (BCrypt 암호화 저장, 해시는 60자)</li>
 *     <li>email : 학교 이메일 (unique, 인증용)</li>
 *     <li>university : 자신의 대학교</li>
 *     <li>nickname : 닉네임 (unique, 서비스 내 활동명)</li>
 *     <li>grade : 학년 (예: 1 ~ 4)</li>
 *     <li>{@link PositionType} : 자신이 맡고 싶은 포지션 (백엔드, 프론트엔드 등) 1개 선택</li>
 *     <li>{@link Skill} : 자신이 가지고 있는 기술 스택 </li>
 *     <li>shortIntro : 짧은 자기소개 (Markdown)</li>
 *     <li>{@link UserRoleType} : 사용자 권한 (ROLE_USER, ROLE_ADMIN 등)</li>
 *     <li>createdAt, updateAt : 생성/수정 시간 (Auditing)</li>
 * </ul>
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private University university;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Integer grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PositionType position;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "skill", nullable = false)
    private Set<Skill> skills;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String shortIntro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleType role;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    private User(
            String username, String password, String email, String nickname,
            University university, Integer grade, PositionType position,
            Set<Skill> skills, String shortIntro, UserRoleType role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.university = university;
        this.nickname = nickname;
        this.grade = grade;
        this.position = position;
        this.skills = skills;
        this.shortIntro = shortIntro;
        this.role = role;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}