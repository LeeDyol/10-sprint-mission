package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성
    @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1")
    @PostMapping
    public ResponseEntity<ReadStatusEntity> create (@RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatusEntity newReadStatus = readStatusService.create(readStatusCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(newReadStatus);
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId")
    @GetMapping
    public ResponseEntity<List<ReadStatusEntity>> findByUserId(@RequestParam UUID userId) {
        List<ReadStatusEntity> readStatus = readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(readStatus);
    }

    // 특정 채널의 메시지 수신 정보 수정
    @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusEntity> update(@PathVariable UUID readStatusId,
                                                   @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {

        ReadStatusEntity updateReadStatus = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity.ok(updateReadStatus);
    }
}
