package com.wagglex2.waggle.domain.user.entity;

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

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String shortIntro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleType role;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    private User(String username, String password, String email, String nickname, String shortIntro, UserRoleType role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.shortIntro = shortIntro;
        this.role = role;
    }
}
