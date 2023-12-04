package com.sima.dms.service;

import com.sima.dms.domain.dto.ProfileDto;
import com.sima.dms.domain.dto.response.BadgeResponseDto;
import com.sima.dms.domain.dto.response.ProfileSearchResponseDto;
import com.sima.dms.domain.dto.request.AdvanceProfileSearchDto;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.enums.RoleEnum;
import com.sima.dms.domain.dto.response.PersonnelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProfileService {

    ProfileDto create(PersonnelResponseDto profileDto);

    ProfileDto update(PersonnelResponseDto updateProfileDto);

    ProfileDto findOne(String personelUsername);

    ProfileDto findById(Long profileId);

    Profile findByCurrentId(Long id);

    Page<ProfileDto> findAll(Pageable pageable);

    Profile checkProfile(PersonnelResponseDto personnelResponseDto);

    List<ProfileDto> assignBranchToUsers(Long branchId, List<String> usernames);

    List<ProfileDto> findAllByRole(List<RoleEnum> roles);

    String getBranchCode(String personelUserName);

    Page<ProfileSearchResponseDto> advanceSearch(AdvanceProfileSearchDto searchDto, Pageable pageable);


    BadgeResponseDto getBadge();
}
