package com.sima.dms.domain.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Data
public abstract class BaseDto {

    private Long id;

    @Schema(hidden = true)
    private String createByFullName;

    @Schema(hidden = true)
    private Long createdById;

    @Schema(hidden = true)
    private Long lastModifiedById;

    @Schema(hidden = true)
    private Instant registerDate;

    @Schema(hidden = true)
    private Instant lastModifiedDate;

    @Schema(hidden = true)
    private boolean active = true;

}
