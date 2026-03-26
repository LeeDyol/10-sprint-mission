package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class BinaryContentFileProcessingErrorException extends BinaryContentException {
    public BinaryContentFileProcessingErrorException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BinaryContentFileProcessingErrorException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
