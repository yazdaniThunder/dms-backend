package com.sima.dms.domain.dto.document;

import com.sima.dms.domain.dto.common.BaseDto;
import com.sima.dms.domain.enums.DocumentRequestStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DocumentRequestStateDto extends BaseDto {

    private DocumentRequestStateEnum state;

    private String description;

    @Schema(hidden = true)
    private Boolean seen;

}
