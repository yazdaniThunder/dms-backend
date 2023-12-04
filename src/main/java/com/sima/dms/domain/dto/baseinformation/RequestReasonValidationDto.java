package com.sima.dms.domain.dto.baseinformation;

import com.sima.dms.domain.enums.FieldNameEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "RequestReasonValidationObject")
public class RequestReasonValidationDto {

    private  Long id;

    private FieldNameEnum fieldName;

    private Boolean required = false;
}
