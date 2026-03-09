package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private String code;
    private int status;
    private String message;
}
