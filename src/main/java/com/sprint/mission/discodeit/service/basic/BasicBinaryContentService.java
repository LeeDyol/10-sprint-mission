package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    private final BinaryContentMapper binaryContentMapper;

    private final BinaryContentStorage localBinaryContentStorage;

    // 첨부 파일 생성
    @Override
    @Transactional
    public BinaryContentDto create(String fileName, byte[] bytes, String contentType) {
        BinaryContentEntity newBinaryContent = new BinaryContentEntity(fileName, bytes.length, contentType);

        binaryContentRepository.save(newBinaryContent);
        localBinaryContentStorage.put(newBinaryContent.getId(), bytes);

        return binaryContentMapper.toDto(newBinaryContent);
    }

    // 첨부 파일 단건 조회
    @Override
    public BinaryContentDto findById(UUID binaryContentId) {
        BinaryContentEntity targetBinaryContent = getBinaryContentEntityOrThrow(binaryContentId);

        return binaryContentMapper.toDto(targetBinaryContent);
    }

    // 첨부 파일 다건 조회
    @Override
    public List<BinaryContentDto> findAllByIds(List<UUID> binaryContentIds) {
        return binaryContentRepository.findAllByIdIn(binaryContentIds).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    // 첨부 파일 전체 조회
    @Override
    public List<BinaryContentDto> findAll() {
        return binaryContentRepository.findAll().stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    // 첨부 파일 삭제
    @Override
    @Transactional
    public void delete(UUID binaryContentId) {
        BinaryContentEntity targetBinaryContent = getBinaryContentEntityOrThrow(binaryContentId);

        binaryContentRepository.delete(targetBinaryContent);
    }

    // 첨부 파일 엔티티 반환
    @Override
    public BinaryContentEntity getBinaryContentEntityOrThrow(UUID binaryContentId) {
        return binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new BinaryContentNotFoundException(binaryContentId));
    }
}
