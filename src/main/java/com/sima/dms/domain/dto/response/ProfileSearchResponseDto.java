package com.sima.dms.domain.dto.response;

import com.sima.dms.domain.enums.BranchTypeEnum;
import com.sima.dms.domain.enums.RoleEnum;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(value = "ProfileSearchResponseObject")
public class ProfileSearchResponseDto {
    private Long ProfileId;
    private Long userId;
    private Long personCode;
    private String nationalKey;
    private String firstName;
    private String lastName;
    private String fullName;
    private String personelUserName;
    private Long branchCode;
    private String branchName;
    private BranchTypeEnum type;
    private RoleEnum role;
    private Boolean active;

}
