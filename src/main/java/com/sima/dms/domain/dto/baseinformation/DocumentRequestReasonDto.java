package com.sima.dms.domain.dto.baseinformation;

import com.sima.dms.domain.dto.common.BaseDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(value = "DocumentRequestReasonObject")
public class DocumentRequestReasonDto extends BaseDto {
    private String title;

    private List<RequestReasonValidationDto> requestReasonValidations;
}
