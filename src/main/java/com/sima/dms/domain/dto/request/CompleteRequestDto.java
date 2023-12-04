package com.sima.dms.domain.dto.request;

import com.sima.dms.domain.enums.WorkflowOperation;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "CompleteRequestObject")
public class CompleteRequestDto {

    private List<Long> ids;
    private WorkflowOperation operation;
    private String description;
}
