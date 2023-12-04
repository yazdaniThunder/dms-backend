package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.BranchDto;
import com.sima.dms.domain.dto.UserDto;
import com.sima.dms.domain.dto.response.AssignedBranchDto;
import com.sima.dms.domain.dto.response.BranchListDto;
import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.User;
import com.sima.dms.domain.enums.BranchTypeEnum;
import com.sima.dms.domain.enums.RoleEnum;
import com.sima.dms.repository.BranchRepository;
import com.sima.dms.repository.ProfileRepository;
import com.sima.dms.repository.UserRepository;
import com.sima.dms.service.BranchService;
import com.sima.dms.service.ProfileService;
import com.sima.dms.service.UserService;
import com.sima.dms.service.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.sima.dms.domain.entity.session.Authorized.currentUserId;
import static com.sima.dms.domain.enums.RoleEnum.*;
import static com.sima.dms.utils.Responses.notFound;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final BranchRepository branchRepository;

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto updateUser) {

        User actual = userRepository.findById(id)
                .orElseThrow(() -> notFound("User not found"));

        if (updateUser.getFirstName() != null)
            actual.setFirstName(updateUser.getFirstName());

        if (updateUser.getLastName() != null)
            actual.setLastName(updateUser.getLastName());

//        if (updateUser.getPassword() != null)
//            actual.updatePassword(updateUser.getPassword());

        return userMapper.toDto(userRepository.save(actual));
    }

    @Override
//    @Transactional(readOnly = true)
    public UserDto findOne(Long id) {
        return userMapper.toDto(userRepository.findById(id).orElseThrow(() -> notFound("Not found")));
    }

    @Override
//    @Transactional(readOnly = true)
    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getDocumentOfficeUsers() {
        return profileRepository.getUsersByRoles(Arrays.asList(RoleEnum.DOA, DOEU,DOPU)).stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getBranchUsers() {
        return profileRepository.getUsersByRoles(Arrays.asList(BA, BU)).stream().map(userMapper::toDto).collect(Collectors.toList());
    }


    @Override
    public List<UserDto> getRegistrar() {

        Long branchId = profileRepository.getBranchId(currentUserId());
        RoleEnum role = profileRepository.getRole(currentUserId());
        BranchTypeEnum branchType = branchRepository.getBranchType(branchId);

        if (role.equals(BU)) {

            if (branchType.equals(BranchTypeEnum.BRANCH)) {
                return userMapper.toDto(profileRepository.getByBranchIdAndRole(Arrays.asList(BU,BA), branchId));
            } else
                return userMapper.toDto(profileRepository.getByBranchIdsAndRoles(Arrays.asList(BU,BA), Collections.singletonList(branchRepository.getParentId(branchId))));

        } else if (role.equals(BA)) {
            return userMapper.toDto(profileRepository.getByBranchIdAndRole(Arrays.asList(BU,BA), branchId));

        } else if (role.equals(DOEU) || role.equals(DOPU)) {
            List<Long> assignedBranches = profileRepository.getAssignedBranches(currentUserId()).stream().map(AssignedBranchDto::getId).collect(Collectors.toList());
            return userMapper.toDto(profileRepository.getByBranchIdsAndRoles(Arrays.asList(BU,BA), assignedBranches));

        } else {
            return userMapper.toDto(profileRepository.getUsersByRoles(Arrays.asList(BU,BA)));
        }

    }

    @Override
    public List<UserDto> getConfirmer() {

        RoleEnum role = profileRepository.getRole(currentUserId());
        Long branchId = profileRepository.getBranchId(currentUserId());
        BranchTypeEnum branchType = profileRepository.getBranchType(currentUserId());
        if (role.equals(BU)) {
            if (branchType.equals(BranchTypeEnum.BRANCH)) {
                return userMapper.toDto(profileRepository.getByBranchIdAndRole(BA, Collections.singletonList(branchId)));
            } else
                return userMapper.toDto(profileRepository.getByBranchIdAndRole(BA, Collections.singletonList(branchRepository.getParentId(branchId))));
        } else if (role.equals(BA)) {
            return userMapper.toDto(profileRepository.getByBranchIdAndRole(BA, Collections.singletonList(branchId)));
        } else if (role.equals(DOEU) || role.equals(DOPU)) {
            List<Long> assignedBranches = profileRepository.getAssignedBranches(currentUserId()).stream().map(AssignedBranchDto::getId).collect(Collectors.toList());
            return userMapper.toDto(profileRepository.getByBranchIdAndRole(BA, assignedBranches));
        } else {
            return userMapper.toDto(profileRepository.getUsersByRole(BA));
        }
    }


    @Override
    public void activeAndDeActiveUser(Long userId, Boolean active) {
        log.debug("Request to update user : {}", userId, active);
        userRepository.activeAndDeActiveProfile(userId, active);
    }

}
