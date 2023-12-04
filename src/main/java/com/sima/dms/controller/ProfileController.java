package com.sima.dms.controller;

import com.sima.dms.domain.dto.ProfileDto;
import com.sima.dms.domain.dto.response.BadgeResponseDto;
import com.sima.dms.domain.dto.response.ProfileSearchResponseDto;
import com.sima.dms.domain.dto.request.AdvanceProfileSearchDto;
import com.sima.dms.domain.dto.response.PersonnelResponseDto;
import com.sima.dms.domain.enums.RoleEnum;
import com.sima.dms.service.ProfileService;
import com.sima.dms.utils.Responses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sima.dms.utils.Responses.noContent;
import static com.sima.dms.utils.Responses.ok;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
@Tag(name = "Profiles")
@RequestMapping("/dms/profile")
public class ProfileController {

    private final ProfileService profileService;

    private final Logger log = LoggerFactory.getLogger(ProfileController.class);

    @CrossOrigin
    @PostMapping
    @ResponseStatus(CREATED)
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Register a new user profile", description = "Returns the new profile")
    public ResponseEntity<ProfileDto> save(@RequestBody @Validated PersonnelResponseDto profileDto) {
        return ResponseEntity.ok().body(profileService.create(profileDto));
    }

//    @CrossOrigin
//    @PutMapping("/{id}")
//    @SecurityRequirement(name = "token")
//    @Operation(summary = "Update user data")
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
//    public ResponseEntity<ProfileDto> update(@RequestBody @Validated ProfileDto profileDto) {
//        return ok(profileService.create(profileDto));
//    }

    @CrossOrigin
    @PutMapping("/update")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Update user data")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ProfileDto> updateProfile(@RequestBody @Validated PersonnelResponseDto updateProfileDto)  {
        return Responses.ok(profileService.update(updateProfileDto));
    }

    @CrossOrigin
    @GetMapping("/{personelUsername}")
    @Operation(summary = "Show user info")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ProfileDto> findOne(@PathVariable String personelUsername) {
        return Responses.ok(profileService.findOne(personelUsername));
    }

    @CrossOrigin
    @GetMapping("getById/{profileId}")
    @Operation(summary = "Show profile info")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA','BU','DOA','DOPU','DOEU','RU')")
    public ResponseEntity<ProfileDto> findById(@PathVariable Long profileId) {
        return Responses.ok(profileService.findById(profileId));
    }

    @CrossOrigin
    @GetMapping
    @Operation(summary = "Returns a list of users")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<ProfileDto>> findAll(Pageable pageable) {
        return Responses.ok(profileService.findAll(pageable));
    }

    @CrossOrigin
    @PutMapping("/{branchId}/assignBranch")
    @Operation(summary = "Assign branch to users")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    public ResponseEntity<List<ProfileDto>> assignBranchToUsers(@PathVariable Long branchId, @RequestBody List<String> personelUsernames) {
        List<ProfileDto> profiles = profileService.assignBranchToUsers(branchId, personelUsernames);
        return Responses.ok(profiles);
    }

    @CrossOrigin
    @PostMapping("/role")
    @Operation(summary = "Get users by roles")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOEU','DOA','BU','BA')")
    public ResponseEntity<List<ProfileDto>> getUsersByRole(@RequestBody List<RoleEnum> roles) {
        List<ProfileDto> profiles = profileService.findAllByRole(roles);
        return Responses.ok(profiles);
    }

    @CrossOrigin
    @PostMapping("/advanceSearch")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Advance search on Profile")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<ProfileSearchResponseDto>> advanceSearch(@RequestBody AdvanceProfileSearchDto searchDto, Pageable pageable) {
        log.debug("REST request advance search on profiles : {}");
        Page<ProfileSearchResponseDto> profileDtos = profileService.advanceSearch(searchDto, pageable);
        return ResponseEntity.ok().body(profileDtos);
    }

    @CrossOrigin
    @GetMapping("/getBadge")
    @Operation(summary = "Return badge ")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('BU', 'BA')")
    public ResponseEntity<BadgeResponseDto> getBadge() {
        return Responses.ok(profileService.getBadge());
    }


}