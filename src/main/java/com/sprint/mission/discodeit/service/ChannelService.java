package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName);

    void deleteChannel(UUID id);

    Channel findChannelByChannelId(UUID id);

    List<Channel> findAllChannels();
    List<Channel> findChannelByUserId(UUID userID);

    Channel updateChannel(UUID id, String channelName);

    void joinChannel(UUID userID, UUID channelID);

    void leaveChannel(UUID userID, UUID channelID);
}
