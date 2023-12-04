package com.sima.dms.service;


import com.sima.dms.domain.dto.request.PersonnelRequestDto;
import com.sima.dms.domain.dto.response.PersonnelResponseDto;

public interface PersonnelService {
    PersonnelResponseDto getPersonnelInfo(PersonnelRequestDto request);
}