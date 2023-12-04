package com.sima.dms.domain.dto.baseinformation;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@ApiModel(value = "FileStatusObject")
public class FileStatusDto {
    private Long id;

    private String title;

    @Schema(hidden = true)
    private String fileType;

    private Boolean isDefault = false;

    private Boolean basic=false;
}
