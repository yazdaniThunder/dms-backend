package com.sima.dms.controller;

import com.sima.dms.domain.dto.request.LonginRequestDto;
import com.sima.dms.domain.entity.session.RefreshTokenRequest;
import com.sima.dms.domain.entity.session.RefreshTokenResponse;
import com.sima.dms.domain.entity.session.TokenResponse;
import com.sima.dms.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.sima.dms.utils.Responses.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@AllArgsConstructor
@Tag(name = "Authentication")
@RequestMapping("/dms/sessions")
public class SessionController {

    private final TokenService tokenService;

    @CrossOrigin
    @PostMapping("/login")
    @Operation(summary = "Create a jwt token")
    public ResponseEntity<TokenResponse> create(@RequestBody @Valid LonginRequestDto request) {
        final TokenResponse token = tokenService.create(request);
        return ok(token);
    }

    @CrossOrigin
    @PostMapping("/refresh")
    @Operation(summary = "Create a new jwt token from refresh code")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        final RefreshTokenResponse token = tokenService.refresh(request);
        return ok(token);
    }

    @CrossOrigin
    @PostMapping("/logout")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Disable jwt token")
    public ResponseEntity<Void> logout() {
        tokenService.logout();
        return noContent();
    }
}
