package com.sprint.mission.discodeit.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class Pageable {
    private int page = 0;
    private int size = 50;
    private List<String> sort;
}
