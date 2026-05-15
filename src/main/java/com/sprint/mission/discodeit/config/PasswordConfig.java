package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
    PasswordConfig
    -------------
    비밀번호를 암호화 하는 설정 파일
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호(Password) 단방향 암호화
        return new BCryptPasswordEncoder();
    }

}
