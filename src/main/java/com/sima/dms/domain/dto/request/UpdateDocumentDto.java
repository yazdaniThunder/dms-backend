package com.sima.dms.domain.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NonNull;

@Data
@ApiModel(value = "UpdateDocumentObject")
public class UpdateDocumentDto {
    @NonNull
    private Long requestId;
    @NonNull
    private String uuid;
}
