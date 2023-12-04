package com.sima.dms.domain.dto.response;

import com.sima.dms.domain.enums.BranchTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BranchListDto {
    private long id;
    private String branchName;
    private long branchCode;
    private BranchTypeEnum type;
    private Long parentCode;
    private String parentName;
}
