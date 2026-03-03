package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;                    // 이메일 (변경 불가능)
    private String username;                 // 닉네임 (변경 가능)
    private String password;                 // 비밀번호 (변경 가능)
    private UUID profileId;                  // 사용자의 프로필 고유 id

    public UserEntity(UserCreateRequest userCreateRequest) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.email = userCreateRequest.email();
        this.password = userCreateRequest.password();
        this.username = userCreateRequest.username();
    }

    public void updateUsername(String newUsername) {
        this.username = newUsername;
        this.updatedAt = Instant.now();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.updatedAt = Instant.now();
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
        this.updatedAt = Instant.now();
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }
}
