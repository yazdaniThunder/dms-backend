package com.sima.dms.service;

import com.sima.dms.domain.dto.request.LonginRequestDto;
import com.sima.dms.domain.entity.session.RefreshTokenRequest;
import com.sima.dms.domain.entity.session.RefreshTokenResponse;
import com.sima.dms.domain.entity.session.TokenResponse;

public interface TokenService {

    TokenResponse create(LonginRequestDto request);

    RefreshTokenResponse refresh(RefreshTokenRequest request);

    void logout();
}
