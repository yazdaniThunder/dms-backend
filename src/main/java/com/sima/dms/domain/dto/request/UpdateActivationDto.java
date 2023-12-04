package com.sima.dms.domain.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "UpdateActivationObject")
public class UpdateActivationDto {
    private List<Long> reasonIds;
    private boolean active;
}
