package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "binary_contents")
public class BinaryContentEntity extends BaseEntity {    // 수정 불가능 클래스
    @Column(nullable = false)
    private String fileName;                             // 파일 이름

    @Column(nullable = false)
    private Long size;                                   // 파일 크기

    private String contentType;                          // 파일 종류

    @Column(nullable = false)
    private byte[] bytes;                                // 실제 파일

    public BinaryContentEntity(String originalFilename, byte[] bytes, String contentType) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.fileName = originalFilename;
        this.bytes = bytes;
        this.contentType = contentType;
        this.size = (long) this.bytes.length;
    }
}