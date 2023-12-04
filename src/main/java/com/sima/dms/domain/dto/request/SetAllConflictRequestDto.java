package com.sima.dms.domain.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SendAllConflictRequestObject")
public class SetAllConflictRequestDto {

    private List<Long> documentSetIds;
    private String description;
    private List<Long> conflictReasons;
}
