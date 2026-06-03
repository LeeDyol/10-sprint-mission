package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
    TODO: Swagger API 명세서 세부 작업
 */
@Slf4j
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "CSRF 토큰 발급", operationId = "token")
    @GetMapping("/csrf-token")
    public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
        String tokenValue = csrfToken.getToken();

        log.debug("CSRF 토큰 요청: {}", tokenValue);

        return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
    }

    @Operation(summary = "현재 로그인 한 사용자 정보 조회", operationId = "getCurrentUser")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getAuthenticatedUser(@AuthenticationPrincipal DiscodeitUserDetails discodeitUserDetails) {
        // 데이터베이스에서 사용자 정보 조회
        UUID userId = discodeitUserDetails.getUserDto().id();
        UserDto currentUser = userService.findById(userId);

        return ResponseEntity.ok(currentUser);
    }

    @Operation(summary = "사용자 권한 수정", operationId = "updateRole")
    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole(@Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {
        UserDto response = authService.updateUserRole(roleUpdateRequest);

        return ResponseEntity.ok(response);
    }
}
