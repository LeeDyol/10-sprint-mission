package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class BaseUpdatableEntity extends BaseEntity {
    @LastModifiedDate                   // 해당 필드 변경 시간 자동 기록
    protected Instant updatedAt;
}
