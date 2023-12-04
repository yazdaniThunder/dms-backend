package com.sima.dms.service.impl;

import com.sima.dms.constants.Security;
import com.sima.dms.domain.dto.ProfileDto;
import com.sima.dms.domain.dto.request.LonginRequestDto;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.session.RefreshToken;
import com.sima.dms.domain.entity.session.RefreshTokenRequest;
import com.sima.dms.domain.entity.session.RefreshTokenResponse;
import com.sima.dms.domain.entity.session.TokenResponse;
import com.sima.dms.repository.ProfileRepository;
import com.sima.dms.repository.RefreshTokenRepository;
import com.sima.dms.service.LDAPService;
import com.sima.dms.service.ProfileService;
import com.sima.dms.service.TokenService;
import com.sima.dms.service.mapper.ProfileMapper;
import com.sima.dms.utils.Messages;
import com.sima.dms.utils.Responses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

import static com.sima.dms.domain.entity.session.Authorized.currentUserId;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;

@Slf4j
@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final ProfileMapper profileMapper;

    private final LDAPService ldapService;
    private final ProfileService profileService;

    private final ProfileRepository profileRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public TokenResponse create(LonginRequestDto request) {

        if (true) {
            final Profile session = profileRepository.findByUser_PersonelUserNameAndActiveIsTrue(request.getUsername().toLowerCase());

            if (session == null) {
                throw Responses.forbidden("user not found");
            }
            if (!session.getUser().isActive())
                throw Responses.forbidden("The user has been disabled");
            log.info(request.getUsername() + " user logged in at: " + Instant.now());
            return create(profileMapper.toDto(session));

        } else
            throw Responses.forbidden(Messages.message(com.sima.dms.constants.Messages.CREATE_SESSION_ERROR_MESSAGE));
    }

    private TokenResponse create(ProfileDto profileDto) {

        final LocalDateTime now = now();
        final LocalDateTime expiresIn = now.plusMinutes(Security.TOKEN_EXPIRATION_IN_MINUTES);

        final String token = Security.JWT.encode(profileDto, expiresIn, Security.TOKEN_SECRET);
        final RefreshToken refresh = new RefreshToken(profileDto, Security.REFRESH_TOKEN_EXPIRATION_IN_HOURS);

        refreshTokenRepository.disableOldRefreshTokens(profileDto.getId());
        refreshTokenRepository.save(refresh);

        return new TokenResponse(
                profileDto,
                token,
                refresh,
                expiresIn
        );
    }

    @Override
    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {

        RefreshToken old = refreshTokenRepository.findOptionalByCodeAndAvailableIsTrue(request.getRefresh())
                .filter(RefreshToken::nonExpired)
                .orElseThrow(() -> Responses.forbidden(Messages.message(com.sima.dms.constants.Messages.REFRESH_SESSION_ERROR_MESSAGE)));

        LocalDateTime now = now();
        LocalDateTime expiresIn = now.plusMinutes(Security.TOKEN_EXPIRATION_IN_MINUTES);
        String token = Security.JWT.encode(old.getProfile(), expiresIn, Security.TOKEN_SECRET);

        refreshTokenRepository.disableOldRefreshTokens(old.getProfile().getId());

        RefreshToken refresh = refreshTokenRepository.save(new RefreshToken(old.getProfile(), Security.REFRESH_TOKEN_EXPIRATION_IN_HOURS));

        return new RefreshTokenResponse(
                token,
                refresh,
                expiresIn
        );
    }

    @Override
    public void logout() {
        Long currentUserId = currentUserId();
        if (!isNull(currentUserId)){
            refreshTokenRepository.disableOldRefreshTokens(currentUserId);
            log.info(profileRepository.getPersonelUserName(currentUserId) + " user logged out at: " + Instant.now());
        }
    }
}
