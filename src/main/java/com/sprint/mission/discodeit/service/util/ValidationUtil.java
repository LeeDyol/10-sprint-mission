package com.sprint.mission.discodeit.service.util;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class ValidationUtil {
    // 유효성 검사 (중복)
    public static void validateDuplicateValue(String currentValue, String updateValue) {
        if (currentValue.equals(updateValue)) {
            throw new DiscodeitException(
                    ErrorCode.DUPLICATE_VALUE_NOT_UPDATE,
                    Map.of(
                            "currentValue", currentValue,
                            "updateValue", updateValue
                    )
            );
        }
    }
}