package com.sima.dms.domain.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "UpdateDocumentRequestDtoObject")
public class UpdateDocumentRequestDto {

    private Long documentId;
    private String maintenanceCode;
}
