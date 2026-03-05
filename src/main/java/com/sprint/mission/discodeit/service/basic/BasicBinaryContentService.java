package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
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

    private final BinaryContentMapper binaryContentMapper;

    // 첨부 파일 생성
    @Override
    public BinaryContentDto create(String fileName, byte[] bytes, String contentType) {
        BinaryContentEntity newBinaryContent = new BinaryContentEntity(fileName, bytes, contentType);
        binaryContentRepository.save(newBinaryContent);

        return binaryContentMapper.toResponseDTO(newBinaryContent);
    }

    // 첨부 파일 단건 조회
    @Override
    public BinaryContentDto findById(UUID targetBinaryContentId) {
        BinaryContentEntity targetBinaryContent = this.getBinaryContentEntityOrThrow(targetBinaryContentId);

        return binaryContentMapper.toResponseDTO(targetBinaryContent);
    }

    // 첨부 파일 다건 조회
    @Override
    public List<BinaryContentDto> findAllByIds(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAll().stream()
                .filter(binaryContentEntity -> binaryContentIds.contains(binaryContentEntity.getId()))
                .map(binaryContentMapper::toResponseDTO)
                .toList();
    }

    // 첨부 파일 전체 조회
    @Override
    public List<BinaryContentDto> findAll() {
        return binaryContentRepository.findAll().stream()
                .map(binaryContentMapper::toResponseDTO)
                .toList();
    }

    // 첨부 파일 삭제
    @Override
    public void delete(UUID targetBinaryContentId) {
        BinaryContentEntity targetBinaryContent = this.getBinaryContentEntityOrThrow(targetBinaryContentId);
        binaryContentRepository.delete(targetBinaryContent);
    }

    // 첨부 파일 엔티티 반환
    @Override
    public BinaryContentEntity getBinaryContentEntityOrThrow(UUID targetBinaryContentId) {
        return binaryContentRepository.findById(targetBinaryContentId)
                .orElseThrow(() -> new IllegalArgumentException("BinaryContent with id {binaryContentId} not found"));
    }
}
