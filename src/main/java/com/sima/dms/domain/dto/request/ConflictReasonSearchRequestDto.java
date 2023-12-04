package com.sima.dms.domain.dto.request;

import com.sima.dms.domain.enums.ConflictTypeEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "ConflictReasonSearchRequestObject")
public class ConflictReasonSearchRequestDto {

    private Long userId;
    private String reason;
    private String regDateFrom;
    private String regDateTo;
    private DocumentSetTypeEnum documentSetType;
    private ConflictTypeEnum type;
}
