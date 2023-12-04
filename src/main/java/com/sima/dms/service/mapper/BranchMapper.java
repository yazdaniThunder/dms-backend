package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.BranchDto;
import com.sima.dms.domain.entity.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class})
public interface BranchMapper extends EntityMapper<BranchDto, Branch> {

    @Mapping(source = "parent.id", target = "parentId")
    BranchDto toDto(Branch branch);

    Branch toEntity(BranchDto branchDto);

    default Branch fromId(Long id) {
        if (id == null) {
            return null;
        }
        Branch branch = new Branch();
        branch.setId(id);
        return branch;
    }
}
