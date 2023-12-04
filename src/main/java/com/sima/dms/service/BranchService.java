package com.sima.dms.service;

import com.sima.dms.domain.dto.BranchDto;
import com.sima.dms.domain.dto.response.AssignedBranchDto;
import com.sima.dms.domain.dto.response.BranchListDto;
import com.sima.dms.domain.enums.BranchTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BranchService {

    BranchDto save(BranchDto category);

    BranchDto update(BranchDto category);

    BranchDto findOne(Long id);

    Page<BranchDto> getAll(Pageable pageable);

    List<BranchListDto> getBranchList();

    void delete(Long id);

    List<BranchDto> getProfileBranches(Long ProfileId);

    void deleteUsers(Long branchId, List<Long> userIds);

    Long getSequence();

    void setPathInBranch();

    Page<BranchDto> getBranchWithProfile( Long branchId, Long profileId,Pageable pageable);

    List<AssignedBranchDto> getAssignedBranch();

    List<BranchListDto> getAllByType(BranchTypeEnum type);
}
