package com.sima.dms.domain.dto.request;
import com.sima.dms.domain.enums.WorkflowOperationState;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
@Data
@ApiModel(value = "CompleteDocumentRequestObject")
public class CompleteDocumentRequestDto {

    private List<Long> ids;
    private WorkflowOperationState operation;
    private String description;
}
