package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.RoleDto;
import com.sima.dms.domain.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface RoleMapper extends EntityMapper<RoleDto, Role> {

    RoleDto toDto(Role role);

    Role toEntity(RoleDto roleDto);

    default Role fromId(Long id) {
        if (id == null) {
            return null;
        }
        Role role = new Role();
        role.setId(id);
        return role;
    }
}
