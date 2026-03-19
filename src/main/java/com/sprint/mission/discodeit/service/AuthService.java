package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;

public interface AuthService {
    // 로그인
    UserDto login(LoginRequest loginRequest);
}
