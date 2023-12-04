package com.sima.dms.domain.entity.session;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sima.dms.domain.dto.ProfileDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "Token", requiredProperties = {"profile", "accessToken", "refreshToken", "expiresIn", "tokenType"})
public class TokenResponse {

    private final String token;
    private final ProfileDto profile;
    private final LocalDateTime expiresIn;
    private final RefreshToken refreshToken;

    public TokenResponse(
            ProfileDto profile,
            String token,
            RefreshToken refreshToken,
            LocalDateTime expiresIn
    ) {
        this.profile = profile;
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public ProfileDto getProfile() {
        return profile;
    }

    @JsonProperty("accessToken")
    public String getToken() {
        return token;
    }

    @JsonProperty("refreshToken")
    public String getRefresh() {
        return refreshToken.getCode();
    }

    @JsonFormat(shape = Shape.STRING)
    @JsonProperty("expiresIn")
    public LocalDateTime getExpiresIn() {
        return expiresIn;
    }

    @JsonProperty("tokenType")
    public String getTokenType() {
        return "Bearer";
    }

}
