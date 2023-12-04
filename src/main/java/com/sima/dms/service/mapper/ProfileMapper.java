package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.BranchDto;
import com.sima.dms.domain.dto.ProfileDto;
import com.sima.dms.domain.entity.Profile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProfileMapper extends EntityMapper<ProfileDto, Profile> {

    @Mapping(target = "assignedBranches", ignore = true)
    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "branch.branchCode", target = "branchCode")
    @Mapping(source = "branch.branchName", target = "branchName")
    @Mapping(source = "branch.superVisorName", target = "superVisorName")
    @Mapping(source = "branch.superVisorCode", target = "superVisorCode")
    @Mapping(source = "branch.type", target = "branchType")
    ProfileDto toDto(Profile profile);

    @Mapping(source = "branchId", target = "branch.id")
    Profile toEntity(ProfileDto profileDto);

    @AfterMapping
    default void AssignedBranches(@MappingTarget ProfileDto dto, Profile entity) {
         if (entity.getBranch().getParent()!=null){
             dto.setParentCode(entity.getBranch().getParent().getBranchCode());
             dto.setParentName(entity.getBranch().getParent().getBranchName());
         }

        if (entity.getAssignedBranches() != null)
            entity.getAssignedBranches().forEach(branch -> {
                dto.addAssignedBranches(new BranchDto(branch));
            });
    }

    default Profile fromId(Long id) {
        if (id == null) {
            return null;
        }
        Profile profile = new Profile();
        profile.setId(id);
        return profile;
    }
}
