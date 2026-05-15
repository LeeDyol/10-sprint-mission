package com.sprint.mission.discodeit.init;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/*
    AdminInitializer
    ----------------
    애플리케이션이 실행될 때, ADMIN 계정 초기화 수행
 */
@Slf4j
@Component
@RequiredArgsConstructor
class AdminInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // 유효성 검사 (ADMIN 권한 존재 여부)
        boolean adminExists = userRepository.existsByRole(Role.ADMIN);

        // ADMIN 계정이 없는 경우에만 생성 및 초기화
        if (!adminExists) {
            UserEntity admin = UserEntity.builder()
                    .username("admin")
                    .email("admin@codeit.com")
                    .password(passwordEncoder.encode("admin1234!"))
                    .build();

            admin.updateRole(Role.ADMIN);

            userRepository.save(admin);
            log.info("초기 ADMIN 계정 생성 완료");
        }
    }
}
