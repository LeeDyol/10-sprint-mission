package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;

public interface AuthService {
    // 사용자 권한 변경
    UserDto updateUserRole(RoleUpdateRequest roleUpdateRequest);
}
