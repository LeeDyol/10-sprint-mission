package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    // 첨부 파일 단건 조회
    @Operation(summary = "첨부 파일 조회", operationId = "find")
    @GetMapping("{binaryContentId}")
    public ResponseEntity<BinaryContentEntity> findById ( @PathVariable UUID binaryContentId){
        BinaryContentEntity binaryContent = binaryContentService.findById(binaryContentId);

        return ResponseEntity.ok(binaryContent);
    }

    // 첨부 파일 전체 조회
    @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn")
    @GetMapping
    public ResponseEntity<List<BinaryContentEntity>> findAllByIds(@RequestParam List<UUID> binaryContentIds) {
        List<BinaryContentEntity> binaryContents = binaryContentService.findAllByIds(binaryContentIds);

        return ResponseEntity.ok(binaryContents);
    }
}
