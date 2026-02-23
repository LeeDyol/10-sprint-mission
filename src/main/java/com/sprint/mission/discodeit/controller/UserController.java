package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 전체 조회
    @Operation(summary = "전체 User 목록 조회", operationId = "findAll")
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll(){
        List<UserDto> users = userService.findAll();

        return ResponseEntity.ok(users);
    }

    // 사용자 생성
    @Operation(summary = "User 등록", operationId = "create")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserEntity> create(@RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
                                             @RequestPart(value = "profile", required = false) MultipartFile profile){
        UserEntity newUser = userService.create(userCreateRequest, profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // 사용자 정보 수정
    @Operation(summary = "User 정보 수정", operationId = "update")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserEntity> update(@PathVariable UUID userId,
                                          @RequestPart UserUpdateRequest userUpdateRequest,
                                          @RequestPart(value = "profile", required = false) MultipartFile profile){

        UserEntity updateUser = userService.update(userId, userUpdateRequest, profile);

        return ResponseEntity.ok(updateUser);
    }

    // 사용자 삭제
    @Operation(summary = "User 삭제", operationId = "delete")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete (@PathVariable UUID userId){
        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

    // 특정 사용자 온라인 상태 변경
    @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId")
    @PatchMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatusEntity> updateByUserId (@PathVariable UUID userId,
                                                            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest){
        UserStatusEntity updatedUserStatus = userStatusService.updateByUserId(userId, userStatusUpdateRequest);

        return ResponseEntity.ok(updatedUserStatus);
    }
}