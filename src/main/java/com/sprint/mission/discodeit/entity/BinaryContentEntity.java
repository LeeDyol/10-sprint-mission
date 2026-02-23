package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContentEntity {                       // 수정 불가능 클래스
    private UUID id;
    private Instant createdAt;                           // 파일 생성 시점
    private String fileName;                             // 파일 이름
    private byte[] bytes;                                // 실제 파일
    private BinaryContentType contentType;               // 파일 종류
    private Long size;                                   // 파일 크기

    public BinaryContentEntity(BinaryContentCreateRequestDTO binaryContentCreateRequestDTO){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = binaryContentCreateRequestDTO.fileName();
        this.bytes = binaryContentCreateRequestDTO.bytes();
        this.contentType = binaryContentCreateRequestDTO.contentType();
        this.size = (long) this.bytes.length;
    }

    public BinaryContentEntity(String originalFilename, byte[] bytes, String contentType) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = originalFilename;
        this.bytes = bytes;
        this.contentType = BinaryContentType.valueOf(contentType);
        this.size = (long) this.bytes.length;
    }
}