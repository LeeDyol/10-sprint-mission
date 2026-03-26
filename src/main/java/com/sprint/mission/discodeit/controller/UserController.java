package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User API")
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @Operation(summary = "전체 User 목록 조회", operationId = "findAll")
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll(){
        List<UserDto> users = userService.findAll();

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "User 등록", operationId = "create")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> create(@Valid @RequestPart(value = "userCreateRequest") UserCreateRequest userCreateRequest,
                                          @RequestPart(value = "profile", required = false) MultipartFile profile){
        log.info("[USER_CREATE] 사용자 생성 요청: username={}, email={}, profile={}",
                userCreateRequest.username(),
                userCreateRequest.email(),
                (profile != null && !profile.isEmpty()) ? profile.getOriginalFilename() : "NONE"
        );

        UserDto newUser = userService.create(userCreateRequest, profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @Operation(summary = "User 정보 수정", operationId = "update")
    @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> update(@PathVariable UUID userId,
                                          @RequestPart UserUpdateRequest userUpdateRequest,
                                          @RequestPart(value = "profile", required = false) MultipartFile profile){
        log.info("[USER_UPDATE] 사용자 정보 수정 요청: newEmail={}, newUsername={}",
                userUpdateRequest.newEmail(),
                userUpdateRequest.newUsername()
        );

        UserDto updatedUser = userService.update(userId, userUpdateRequest, profile);

        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "User 삭제", operationId = "delete")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete (@PathVariable UUID userId){
        log.info("[User_DELETE] 사용자 삭제 요청: id={}", userId);
        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId")
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusDto> updateByUserId (@PathVariable UUID userId,
                                                         @Valid @RequestBody UserStatusUpdateRequest userStatusUpdateRequest){
        UserStatusDto updatedUserStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);

        return ResponseEntity.ok(updatedUserStatus);
    }
}