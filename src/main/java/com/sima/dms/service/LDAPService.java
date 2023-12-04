package com.sima.dms.service;

import com.sima.dms.domain.dto.request.LonginRequestDto;

public interface LDAPService {

    boolean authUser(LonginRequestDto longinRequestDto);
}
