package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
@Slf4j
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    private final BinaryContentStorage binaryContentStorage;

    @Operation(summary = "첨부 파일 조회", operationId = "find")
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> findById (@PathVariable UUID binaryContentId){
        BinaryContentDto response = binaryContentService.findById(binaryContentId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn")
    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAllByIds(@RequestParam List<UUID> binaryContentIds) {
        List<BinaryContentDto> responses = binaryContentService.findAllByIds(binaryContentIds);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "파일 다운로드", operationId = "download")
    @GetMapping("/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
        BinaryContentDto response = binaryContentService.findById(binaryContentId);

        log.info("[BINARY_CONTENT_DOWNLOAD] 첨부파일 다운로드 요청: id={}, filename={}",
                response.id(),
                response.fileName()
        );

        return binaryContentStorage.download(response);
    }
}
