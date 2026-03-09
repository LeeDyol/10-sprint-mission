package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    // 로그인
    public UserDto login(LoginRequest loginRequest) {
       UserEntity targetUser = getUserEntityOrThrow(loginRequest.username());

        if (!targetUser.getPassword().equals(loginRequest.password())) {
            throw new IllegalArgumentException(("Wrong password"));
        }

        return userMapper.toDto(targetUser);
    }

    // 사용자 반환
    public UserEntity getUserEntityOrThrow(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username {" + username + "} not found"));
    }
}