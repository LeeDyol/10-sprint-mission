package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.response.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/*
    DiscodeitUserDetails
    --------------------
    인증을 마친 사용자 정보
 */
@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {
    private final UserDto userDto;
    private final String password;

    // 사용자 권한 (Role) 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = "ROLE_" + userDto.role().name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    // 비밀번호 반환
    @Override
    public String getPassword() {
        return this.password;
    }

    // 사용자 닉네임 반환
    @Override
    public String getUsername() {
        return userDto.username();
    }

    // 게정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;        // 만료 안 됨
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true;        // 잠기지 않음
    }

    // 비밀번호 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;            // 만료 안 됨
    }

    // 계정 활성화 여부 반환
    @Override
    public boolean isEnabled() {
        return true;        // 활성화
    }
}
