package com.sima.dms.domain.dto.document;

import com.sima.dms.domain.dto.common.BaseDto;
import com.sima.dms.domain.enums.OtherDocumentStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OtherDocumentStateDto extends BaseDto {

    private OtherDocumentStateEnum state;

    private String description;

    @Schema(hidden = true)
    private Boolean seen;
}
