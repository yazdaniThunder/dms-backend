package com.sima.dms.domain.dto.documentSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sima.dms.domain.dto.common.BaseDto;
import com.sima.dms.domain.enums.DocumentSetStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentSetStateDto  extends BaseDto {

    @JsonIgnore
    @Schema(hidden = true)
    private Long userId;

    @Schema(hidden = true)
    private String username;

    private DocumentSetStateEnum name;
    private String description;

    @Schema(hidden = true)
    private Boolean seen;

    public DocumentSetStateDto(DocumentSetStateEnum name, Long userId, String description) {
        this.name = name;
        this.userId = userId;
        this.description = description;
    }

}