package com.sprint.mission.discodeit.entity;

public enum BinaryContentType {
    FILE,
    IMAGE,
    PICTURE,
    VOTE;

    public static BinaryContentType fromContentType(String contentType) {
        if (contentType == null) {
            return FILE;
        }

        // IMAGE 매핑
        if (contentType.toLowerCase().startsWith("image")) {
            return IMAGE;
        }

        // 기본적으로 FILE 반환
        try {
            return BinaryContentType.valueOf(contentType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return FILE;
        }
    }
}
