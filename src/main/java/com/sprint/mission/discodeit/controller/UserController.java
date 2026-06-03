package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
        UserDto newUser = userService.create(userCreateRequest, profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @Operation(summary = "User 정보 수정", operationId = "update")
    @PatchMapping(path = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> update(@PathVariable UUID userId,
                                          @Valid @RequestPart UserUpdateRequest userUpdateRequest,
                                          @RequestPart(value = "profile", required = false) MultipartFile profile){
        UserDto updatedUser = userService.update(userId, userUpdateRequest, profile);

        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "User 삭제", operationId = "delete")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete (@PathVariable UUID userId){
        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }
}