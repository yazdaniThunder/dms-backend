package com.sima.dms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PropertyValidationDto {

    @JsonIgnore
    @Schema(hidden = true)
    private Long id;

    @JsonIgnore
    @Schema(hidden = true)
    private String nodePropertyUuid;

    private double similarity;
}