package com.sima.dms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sima.dms.domain.enums.BranchTypeEnum;
import com.sima.dms.domain.enums.RoleEnum;
import com.sima.dms.domain.dto.response.PersonnelResponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(value = "ProfileObject")
public class ProfileDto {

    private Long id;
    @Schema(hidden = true)
    private Long branchId;
    private Long prAcBranch;
    private Long branchCode;
    private String branchName;
    private String superVisorCode;
    private String superVisorName;
    private String parentBranch;

    @Schema(hidden = true)
    private BranchTypeEnum branchType;
    @Schema(hidden = true)
    private Long parentCode;
    @Schema(hidden = true)
    private String parentName;

    private String job;
    private String position;
    private String positionCode;

    private String rankingJobDesc;
    private Long rankingPositionCode;
    private String rankingPositionDesc;

    private RoleEnum role;
    private UserDto user;
    private boolean active = true;
    @JsonIgnore
    private List<BranchDto> assignedBranches = new ArrayList<>();

    public ProfileDto(PersonnelResponseDto dto) {
        this.job = dto.getJob();
        this.position = dto.getPosition();
        this.prAcBranch = dto.getPrAcvBranch();
        this.branchCode = dto.getAbrnchcod();
        this.branchName = dto.getABranchName();
        this.parentBranch = dto.getParentBranch();
        this.positionCode = dto.getPositionCode();
        this.rankingJobDesc = dto.getRankingJobDesc();
        this.rankingPositionDesc = dto.getRankingPositionDesc();
        this.rankingPositionCode = dto.getRankingPositionCode();
        this.active = true;
        this.user = new UserDto(dto);
    }
    public void addAssignedBranches(BranchDto branchDto) {
        this.assignedBranches.add(branchDto);
    }
}
