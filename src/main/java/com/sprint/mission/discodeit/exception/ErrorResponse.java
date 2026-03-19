package com.sprint.mission.discodeit.exception;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse (
    Instant timestamp,
    String code,
    int status,
    String message
) {
}