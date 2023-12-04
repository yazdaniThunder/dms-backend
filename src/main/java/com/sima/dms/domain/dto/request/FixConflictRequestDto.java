package com.sima.dms.domain.dto.request;


import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "FixConflictRequestObject")
public class FixConflictRequestDto {

    private String description;
    private Long documentSetId;
}
