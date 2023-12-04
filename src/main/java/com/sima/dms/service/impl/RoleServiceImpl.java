package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.RoleDto;
import com.sima.dms.domain.enums.RoleEnum;
import com.sima.dms.repository.RoleRepository;
import com.sima.dms.service.RoleService;
import com.sima.dms.service.mapper.RoleMapper;
import com.sima.dms.utils.Responses;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sima.dms.utils.Responses.notFound;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    @Override
    public RoleDto create(RoleDto roleDto) {
        return roleMapper.toDto(roleRepository.save(roleMapper.toEntity(roleDto)));
    }

    @Override
//    @Transactional(readOnly = true)
    public RoleDto findOne(Long id) {
        return roleMapper.toDto(roleRepository
                .findById(id)
                .orElseThrow(() -> Responses.notFound("Not found")));
    }

    @Override
//    @Transactional(readOnly = true)
    public List<RoleDto> findAll() {
        return roleMapper.toDto(roleRepository.findAll());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

//    @Override
//    public RoleEnum getRole(String roleTitle) {
//
//        if (roleTitle.equals(RoleEnum.getPersianTitle(RoleEnum.USER)))
//            return RoleEnum.USER;
//        else if (roleTitle.equals(RoleEnum.getPersianTitle(RoleEnum.ADMIN)))
//            return RoleEnum.ADMIN;
//        else if (roleTitle.equals(RoleEnum.getPersianTitle(RoleEnum.BA)))
//            return RoleEnum.BA;
//        else if (roleTitle.equals(RoleEnum.getPersianTitle(RoleEnum.BU)))
//            return RoleEnum.BU;
//        else if (roleTitle.equals(RoleEnum.getPersianTitle(RoleEnum.DOA)))
//            return RoleEnum.DOA;
//        else if (roleTitle.equals(RoleEnum.getPersianTitle(RoleEnum.DOU)))
//            return RoleEnum.DOU;
//        return null;
//    }

}
