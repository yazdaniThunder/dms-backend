package com.sima.dms.domain.dto.response;

import com.sima.dms.domain.enums.RoleEnum;
import lombok.Data;

@Data
public class PersonnelResponseDto {

    private Long profileId;
    private Long cfCifNo;
    private Long prsnCode;
    private String nationalKey;
    private String personelUserName;

    private Long abrnchcod;
    private Long prAcvBranch;
    private String aBranchName;
    private String parentBranch;

    private String firstName;
    private String lastName;
    private String fullName;
    private String completeName;
    private String fatherName;
    private String position;
    private String job;
    private Long rankingPositionCode;
    private String rankingPositionDesc;
    private String positionCode;
    private String rankingJobDesc;
    private boolean isIntActiveCode;
    private String isIntActiveDesc;
    private RoleEnum role;
    private Long branchId;

}
