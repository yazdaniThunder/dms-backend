package com.sima.dms.service;

import com.sima.dms.domain.dto.RoleDto;
import com.sima.dms.domain.enums.RoleEnum;

import java.util.List;

public interface RoleService {

    RoleDto create(RoleDto roleDto);

    RoleDto findOne(Long id);

    List<RoleDto> findAll();

    void delete(Long id);

//    RoleEnum getRole(String roleTitle);
}
