package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    // 첨부 파일 생성
    @Override
    public BinaryContentEntity create(String fileName, byte[] bytes, String contentType) {
        BinaryContentEntity newBinaryContent = new BinaryContentEntity(fileName, bytes, contentType);
        binaryContentRepository.save(newBinaryContent);

        return newBinaryContent;
    }

    // 첨부 파일 단건 조회
    @Override
    public BinaryContentEntity findById(UUID targetBinaryContentId) {
        return binaryContentRepository.findById(targetBinaryContentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "BinaryContent with id {" + targetBinaryContentId + "} not found"));
    }

    // 첨부 파일 다건 조회
    @Override
    public List<BinaryContentEntity> findAllByIds(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAll().stream()
                .filter(binaryContentEntity -> binaryContentIds.contains(binaryContentEntity.getId()))
                .toList();
    }

    // 첨부 파일 전체 조회
    @Override
    public List<BinaryContentEntity> findAll() {
        return binaryContentRepository.findAll();
    }

    // 첨부 파일 삭제
    @Override
    public void delete(UUID targetBinaryContentId) {
        BinaryContentEntity targetBinaryContent = findById(targetBinaryContentId);
        binaryContentRepository.delete(targetBinaryContent);
    }
}
