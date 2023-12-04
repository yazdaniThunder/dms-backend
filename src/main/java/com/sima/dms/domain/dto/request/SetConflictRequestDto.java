package com.sima.dms.domain.dto.request;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SendConflictRequestObject")
public class SetConflictRequestDto {

    private Long documentSetId;
    private String description;
    private List<Long> conflictReasons;
}
