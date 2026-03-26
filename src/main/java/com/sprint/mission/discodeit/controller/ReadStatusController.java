package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @Operation(summary = "Message 읽음 상태 생성", operationId = "create_1")
    @PostMapping
    public ResponseEntity<ReadStatusDto> create (@Valid @RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatusDto response = readStatusService.create(readStatusCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User의 Message 읽음 상태 목록 조회", operationId = "findAllByUserId")
    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> findByUserId(@RequestParam UUID userId) {
        List<ReadStatusDto> response = readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Message 읽음 상태 수정", operationId = "update_1")
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> update(@PathVariable UUID readStatusId,
                                                @Valid @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {

        ReadStatusDto response = readStatusService.update(readStatusId, readStatusUpdateRequest);
        return ResponseEntity.ok(response);
    }
}
