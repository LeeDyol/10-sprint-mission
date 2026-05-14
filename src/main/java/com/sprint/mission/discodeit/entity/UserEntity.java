package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends BaseUpdatableEntity {
    @Column(nullable = false, unique = true)
    private String username;                                       // 닉네임

    @Column(nullable = false, unique = true)
    private String email;                                          // 이메일

    @Column(nullable = false)
    private String password;                                       // 비밀번호

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(unique = true)
    private BinaryContentEntity profile;                           // 프로필 이미지

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    private UserStatusEntity userStatus;                           // 상태

    @Builder
    public UserEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updateUsername(String newUsername) {
        this.username = newUsername;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    public void updateUserStatus(UserStatusEntity newUserStatus) {
        this.userStatus = newUserStatus;
    }

    public void updateProfile(BinaryContentEntity newProfile) {
        this.profile = newProfile;
    }
}
