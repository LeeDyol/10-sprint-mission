package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    /*
        전체 목록 조회
     */
    // [성공]
    @Test
    @DisplayName("전체 목록 조회 완료")
    void find_all_user_success() throws Exception {
        // given
        UserDto firstUserDto = UserDto.builder().build();
        UserDto secondUserDto = UserDto.builder().build();
        List<UserDto> users = List.of(firstUserDto, secondUserDto);

        given(userService.findAll()).willReturn(users);

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /*
        사용자 등록
     */
    // [성공]
    @Test
    @DisplayName("사용자 등록 완료")
    void create_user_success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "test.png",
                "image/png",
                "image".getBytes()
        );

        // JSON 파일화
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // 생성할 사용자
        UserDto user = UserDto.builder()
                .id(UUID.randomUUID())
                .username(request.username())
                .email(request.email())
                .profile(null)
                .build();

        given(userService.create(any(), any())).willReturn(user);

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .file(profilePart)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(user.id().toString()));
    }

    // [실패] 이메일 형식 오류
    @Test
    @DisplayName("사용자 등록 실패: 잘못된 이메일 형식을 입력한 경우, 400 Bad Request 반환")
    void create_user_failure() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
                "yushi",
                "yushi",
                "yushi1234"
        );
        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "test.png",
                "image/png",
                "image".getBytes()
        );

        // JSON 파일화
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .file(profilePart)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }

    /*
        사용자 정보 수정
     */
    // [성공]
    @Test
    @DisplayName("사용자 정보 수정 완료")
    void update_user_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(
                "tokuno",
                "tokuno@wish.com",
                "tokuno1234"
        );

        // JOSN
        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // 수정할 사용자
        UserDto user = UserDto.builder()
                .id(userId)
                .username(request.newUsername())
                .email(request.newEmail())
                .profile(null)
                .build();
        given(userService.update(eq(userId), any(), any())).willReturn(user);

        // when & then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(requestPart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(request.newUsername()));
    }

    // [실패] 잘못된 사용자 ID 전달
    @Test
    @DisplayName("사용자 정보 수정 실패: 잘못된 사용자 ID를 전달하 경우, 400 Bad Request 반환")
    void update_user_failure() throws Exception {
        // given
        String userId = "userId";
        UserUpdateRequest request = new UserUpdateRequest(
                "tokuno",
                "tokuno@wish.com",
                "tokuno1234"
        );

        // JOSN
        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(requestPart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentTypeMismatchException"));
    }

    /*
        사용자 삭제
     */
    // [성공]
    @Test
    @DisplayName("사용자 삭제 완료")
    void delete_user_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    // [실패] HTTP 메서드 오류
    @Test
    @DisplayName("사용자 삭제 실패: HTTP 메서드가 잘못된 경우, 405 Method Not Allowed 반환")
    void delete_user_failure() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        // when & then
        mockMvc.perform(post("/api/users/{userId}", userId))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.exceptionType").value("HttpRequestMethodNotSupportedException"));
    }

    /*
        사용자 온라인 상태 업데이트
     */
    // [성공]
    @Test
    @DisplayName("사용자 온라인 상태 업데이트 완료")
    void update_user_status_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        Instant lastActiveAt = Instant.now();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(lastActiveAt);

        // 업데이트할 온라인 상태
        UserStatusDto userStatus = UserStatusDto.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .lastActiveAt(request.newLastActiveAt())
                .build();
        given(userStatusService.updateByUserId(eq(userId), any())).willReturn(userStatus);

        // when & then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userStatus.userId().toString()));
    }

    // [실패]
    @Test
    @DisplayName("사용자 온라인 상태 업데이트 실패: ")
    void update_user_status_failure() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(null);

        // when & then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
