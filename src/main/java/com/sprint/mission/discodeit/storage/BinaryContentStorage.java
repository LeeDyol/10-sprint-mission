package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {
    // 첨부 파일 저장
    UUID put(UUID binaryContentId, byte[] bytes);

    // 첨부 파일 변환
    InputStream get(UUID binaryContentId);

    // 첨부 파일 다운로드
    ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}
