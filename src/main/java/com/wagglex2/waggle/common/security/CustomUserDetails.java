package com.wagglex2.waggle.common.security;

import com.wagglex2.waggle.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails, CredentialsContainer {

    private final Long userId;
    private final String username;
    private String password;
    private final String nickname;
    private final String role;

    // JWT에서 직접 생성하는 생성자
    public CustomUserDetails(Long userId, String username, String nickname, String role) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.password = null;
    }

    // 생성자
    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.role = user.getRole().name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(role)
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        this.password = null; // 인증 후 비밀번호 제거
    }
}