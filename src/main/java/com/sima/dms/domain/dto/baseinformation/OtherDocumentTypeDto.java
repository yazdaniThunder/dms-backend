package com.sima.dms.domain.dto.baseinformation;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "OtherDocumentTypeObject")
public class OtherDocumentTypeDto {

    private Long id;

    private String title;

    @Schema(hidden = true)
    private String fileType;

    private Boolean basic=false;
}
