package com.sima.dms.service;

import com.sima.dms.domain.dto.UserDto;
import com.sima.dms.domain.enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserDto create(UserDto createUser);

    UserDto update(Long id, UserDto updateUser);

    UserDto findOne(Long id);

    Page<UserDto> findAll(Pageable pageable);

    void delete(Long id);

    List<UserDto> getDocumentOfficeUsers();

    List<UserDto> getBranchUsers();

    void activeAndDeActiveUser(Long userId, Boolean active);

    List<UserDto> getRegistrar();

    List<UserDto> getConfirmer();
}
