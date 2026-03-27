package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserCreateRequest(
        @NotBlank(message = "사용자 이름을 입력해주세요.")
        @Size(max = 50, message = "사용자 이름은 50자 이내입니다.")
        String username,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식을 입력해주세요.")
        @Size(max = 100, message = "이메일은 100자 이내입니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(max = 60, message = "비밀번호는 60자 이내입니다.")
        String password
) {

}