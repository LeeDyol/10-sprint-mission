package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PageResponseMapper {
    // Slice -> 페이지 응답 DTO 변환
    public <T> PageResponse<T> fromSlice(Slice<T> slice) {
        return PageResponse.<T>builder()
                .content(slice.getContent())
                .number(slice.getNumber())
                .size(slice.getSize())
                .hasNext(slice.hasNext())
                .totalElements(null)
                .build();
    }

    // Page -> 페이지 응답 DTO 변환
    public <T> PageResponse<T> fromPage(Page<T> page){
        return PageResponse.<T>builder()
                .content(page.getContent())
                .number(page.getNumber())
                .size(page.getSize())
                .hasNext(page.hasNext())
                .totalElements(page.getTotalElements())
                .build();
    }
}
