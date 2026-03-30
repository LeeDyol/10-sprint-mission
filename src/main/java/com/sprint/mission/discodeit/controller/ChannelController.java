package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
@Slf4j
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
    @GetMapping
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channels);
    }

    @Operation(summary = "Public Channel 생성", operationId = "create_3")
    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublicChannel(@Valid @RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {
        log.info("[PUBLIC_CHANNEL_CREATE] 공개 채널 생성 요청: name={}, description={}",
                publicChannelCreateRequest.name(), publicChannelCreateRequest.description()
        );

        ChannelDto newChannel = channelService.createPublicChannel(publicChannelCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(newChannel);
    }

    @Operation(summary = "Private Channel 생성", operationId = "create_4")
    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivateChannel(@Valid @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
        log.info("[PRIVATE_CHANNEL_CREATE] 비공개 채널 생성 요청: 총 {}명", privateChannelCreateRequest.participantIds().size());

        ChannelDto newChannel = channelService.createPrivateChannel(privateChannelCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(newChannel);
    }

    @Operation(summary = "Channel 정보 수정", operationId = "update_3")
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDto> update(@PathVariable UUID channelId,
                                             @Valid @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest) {
        log.info("[PUBLIC_CHANNEL_UPDATE] 공개 채널 수정 요청: name={}, description={}",
                publicChannelUpdateRequest.newName(), publicChannelUpdateRequest.newDescription()
        );

        ChannelDto updateChannel = channelService.update(channelId, publicChannelUpdateRequest);

        return ResponseEntity.ok(updateChannel);
    }

    @Operation(summary = "Channel 삭제", operationId = "delete_2")
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        log.info("[CHANNEL_DELETE] 채널 삭제 요청: id={}", channelId);

        channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }
}
