package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;

    private final UserMapper userMapper;

    // 로그인: Security가 DB로부터 사용자가 입력한 사용자 정보를 가져오는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = getUserEntityOrThrow(username);
        boolean isOnline = isUserOnline(user.getUsername());

        UserDto userDto = userMapper.toDto(user, isOnline);

        // 데이터베이스에 저장되어 있던 사용자의 정보 반환
        return new DiscodeitUserDetails(userDto, user.getPassword());
    }

    // 사용자 권한 변경
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateUserRole(RoleUpdateRequest roleUpdateRequest) {
        UserEntity targetUser = getUserEntityOrThrow(roleUpdateRequest.userId());
        boolean isOnline = isUserOnline(targetUser.getUsername());

        targetUser.updateRole(roleUpdateRequest.newRole());

        // 세션 무효화
        expireUserSession(targetUser.getUsername());

        return userMapper.toDto(targetUser, isOnline);
    }

    // 세션 무효화
    private void expireUserSession(String username) {
        sessionRegistry.getAllPrincipals().stream()
                // 현재 접속한 사용자 중 인증된 사용자만 필터링
                .filter(principal -> principal instanceof DiscodeitUserDetails)
                // 로그인 한 사용자 객체를 인증된 사용자 객체로 형 변환
                .map(principal -> (DiscodeitUserDetails) principal)
                // 특정 사용자 객체만 필터링
                .filter(userDetails -> userDetails.getUsername().equals(username))
                // 특정 사용자의 모든 세션 정보
                .flatMap(userDetails -> sessionRegistry.getAllSessions(userDetails, false).stream())
                // 세션 무효화
                .forEach(SessionInformation::expireNow);
    }

    // 사용자 반환 (userId)
    private UserEntity getUserEntityOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // 사용자 반환 (username)
    private UserEntity getUserEntityOrThrow(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    // 사용자 접속 여부 반환: 세션을 기반으로 사용자 접속 여부 반환
    private boolean isUserOnline(String username) {
        return sessionRegistry.getAllPrincipals().stream()
                // 인증된 사용자만 필터링
                .filter(principal -> principal instanceof DiscodeitUserDetails)
                .map(principal -> (DiscodeitUserDetails) principal)
                // 특정 사용자의 세션 정보 유무 확인
                .anyMatch(userDetails -> userDetails.getUsername().equals(username));
    }
}