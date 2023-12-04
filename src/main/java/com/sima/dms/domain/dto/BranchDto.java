package com.sima.dms.domain.dto;

import com.sima.dms.domain.entity.Branch;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(value = "BranchObject")
public class BranchDto {

    private Long id;

    private String branchAddress;

    private Long branchCode;

    private String branchName;

    private String branchType;

    private String cityCode;

    private String cityName;

    private String englishBranchName;

    private String provinceCode;

    private String provinceName;

    private String superVisorCode;

    private String superVisorName;

    private Boolean active = true;

    private String path;

    @Schema(hidden = true)
    private Long parentId;

    private List<ProfileDto> assignedProfiles;

    public BranchDto(Branch branch) {
        this.id = branch.getId();
        this.branchAddress = branch.getBranchAddress();
        this.branchCode = branch.getBranchCode();
        this.branchName = branch.getBranchName();
        this.branchType = branch.getBranchType();
        this.cityCode = branch.getCityCode();
        this.cityName = branch.getCityName();
        this.englishBranchName = branch.getEnglishBranchName();
        this.provinceCode = branch.getProvinceCode();
        this.provinceName = branch.getProvinceName();
        this.superVisorCode = branch.getSuperVisorCode();
        this.superVisorName = branch.getSuperVisorName();
        this.active = branch.getActive();
    }
}
