package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class BinaryContentFileProcessingErrorException extends BinaryContentException {
    public BinaryContentFileProcessingErrorException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BinaryContentFileProcessingErrorException(UUID messageId, String filename) {
        super(ErrorCode.BINARY_CONTENT_FILE_PROCESSING_ERROR,
                Map.of(
                        "messageId", messageId,
                        "filename", filename
                )
        );
    }

    public BinaryContentFileProcessingErrorException(String username, String filename) {
        super(ErrorCode.BINARY_CONTENT_FILE_PROCESSING_ERROR,
                Map.of(
                        "username", username,
                        "filename", filename
                )
        );
    }
}
