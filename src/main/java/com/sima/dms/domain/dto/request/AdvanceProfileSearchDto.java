package com.sima.dms.domain.dto.request;

import com.sima.dms.domain.enums.RoleEnum;
import lombok.Data;

@Data
public class AdvanceProfileSearchDto {

    private Long personCode;
    private String nationalKey;
    private String personelUserName;
    private String fullName;
    private RoleEnum role;
    private String job;
    private Long branchId;
}
