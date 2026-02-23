package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.UserEntity;

public interface AuthService {
    // 로그인
    UserEntity login(LoginRequest loginRequest);
}
