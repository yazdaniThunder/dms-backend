package com.sima.dms.domain.dto.document;

import com.sima.dms.domain.dto.common.BaseDto;
import com.sima.dms.domain.enums.DocumentStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Data
public class DocumentStateDto extends BaseDto {

    @Schema(hidden = true)
    private Long userId;

    @Schema(hidden = true)
    private String username;

    private DocumentStateEnum name;
    private Instant date = Instant.now();
    private String description;

    @Schema(hidden = true)
    private Boolean seen;

    public DocumentStateDto(DocumentStateEnum name, Long userId) {
        this.name = name;
        this.userId = userId;
    }
}