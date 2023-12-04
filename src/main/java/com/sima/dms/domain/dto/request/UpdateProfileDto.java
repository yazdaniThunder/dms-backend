package com.sima.dms.domain.dto.request;

import com.sima.dms.domain.enums.RoleEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "UpdateProfileObject")
public class UpdateProfileDto {

    private Long id;
    private RoleEnum role;
    private Long branchId;
}