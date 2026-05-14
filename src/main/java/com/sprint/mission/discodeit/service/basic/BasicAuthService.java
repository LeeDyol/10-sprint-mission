package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.WrongPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService, UserDetailsService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    // 로그인 한 사용자 정보 조회
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = getUserEntityOrThrow(username);

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())       // 암호화 된 비밀번호 주입
                .roles("USER")
                .build();
    }

    // 로그인
    @Override
    public UserDto login(LoginRequest loginRequest) {
        UserEntity targetUser = getUserEntityOrThrow(loginRequest.username());

        if (!targetUser.getPassword().equals(loginRequest.password())) {
            throw new WrongPasswordException(ErrorCode.WRONG_PASSWORD);
        }

        return userMapper.toDto(targetUser);
    }

    // 사용자 반환
    private UserEntity getUserEntityOrThrow(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        Map.of("username", username)
                ));
    }
}