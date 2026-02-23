package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDTO;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
    @GetMapping
    public ResponseEntity<List<ChannelDTO>> findAllByUserId(@RequestParam UUID userId) {
        List<ChannelDTO> channels = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(channels);
    }

    @Operation(summary = "Public Channel 생성", operationId = "create_3")
    @PostMapping("/public")
    public ResponseEntity<ChannelEntity> createPublicChannel(@RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {
        ChannelEntity newChannel = channelService.createPublicChannel(publicChannelCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(newChannel);
    }

    @Operation(summary = "Private Channel 생성", operationId = "create_4")
    @PostMapping("/private")
    public ResponseEntity<ChannelEntity> createPrivateChannel(@RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
        ChannelEntity newChannel = channelService.createPrivateChannel(privateChannelCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(newChannel);
    }

    @Operation(summary = "Channel 정보 수정", operationId = "update_3")
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelDTO> update(@PathVariable UUID channelId,
                                             @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest) {

        ChannelDTO updateChannel = channelService.update(channelId, publicChannelUpdateRequest);

        return ResponseEntity.ok(updateChannel);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);

        return ResponseEntity.noContent().build();
    }
}
