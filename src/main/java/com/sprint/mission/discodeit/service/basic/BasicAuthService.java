package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    // 로그인
    public UserEntity login(LoginRequest loginRequest) {
        UserEntity targetUser = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User with username {" + loginRequest.username() + "} not found"));

        if (!targetUser.getPassword().equals(loginRequest.password())) {
            throw new IllegalArgumentException(("Wrong password"));
        }

        return targetUser;
    }
}
