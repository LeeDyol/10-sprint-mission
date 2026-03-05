package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

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
    public ResponseEntity<byte[]> download(@PathVariable UUID binaryContentId) {
        BinaryContentEntity response = binaryContentService.getBinaryContentEntityOrThrow(binaryContentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CONTENT_TYPE, response.getContentType())
                .body(response.getBytes());
    }
}
