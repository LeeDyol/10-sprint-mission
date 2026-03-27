package com.sprint.mission.discodeit.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record LoginRequest(
    @NotBlank(message = "사용자 이름을 입력해주세요.")
    @Size(max = 50, message = "사용자 이름은 50자 이내입니다.")
    String username,

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(max = 60, message = "비밀번호는 60자 이내입니다.")
    String password
) {

}
