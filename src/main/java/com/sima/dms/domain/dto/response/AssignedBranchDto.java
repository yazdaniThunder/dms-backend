package com.sima.dms.domain.dto.response;

import com.sima.dms.domain.enums.BranchTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignedBranchDto {
    private Long id;
    private String branchName;
    private Long branchCode;
    private BranchTypeEnum type;
}
