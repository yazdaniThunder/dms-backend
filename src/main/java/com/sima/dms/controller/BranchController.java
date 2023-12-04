package com.sima.dms.controller;

import com.sima.dms.domain.dto.BranchDto;
import com.sima.dms.domain.dto.request.AssignedProfileDto;
import com.sima.dms.domain.dto.response.AssignedBranchDto;
import com.sima.dms.domain.dto.response.BranchListDto;
import com.sima.dms.domain.enums.BranchTypeEnum;
import com.sima.dms.service.BranchService;
import com.sima.dms.service.PermissionService;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.sima.dms.utils.Responses.noContent;

@RestController
@AllArgsConstructor
@Tag(name = "Branches")
@RequestMapping("/dms/branches")
public class BranchController {

    private final BranchService branchService;
    private final PermissionService permissionService;
    private final Logger log = LoggerFactory.getLogger(BranchController.class);

    @CrossOrigin
    @PostMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Create branch")
    public ResponseEntity<BranchDto> create(@RequestBody BranchDto branchDto) {
        log.debug("REST request to create branch : ", branchDto);
        BranchDto branch = branchService.save(branchDto);
        return ResponseEntity.ok().body(branch);
    }

    @CrossOrigin
    @PutMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Update branch")
    public ResponseEntity<BranchDto> update(@RequestBody BranchDto branchDto) {
        log.debug("REST request to update branch : ", branchDto);
        BranchDto branch = branchService.update(branchDto);
        return ResponseEntity.ok().body(branch);
    }

    @CrossOrigin
    @PutMapping("/update")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Update branch")
    public ResponseEntity<List<BranchDto>> update(@RequestBody List<BranchDto> branches) {
        log.debug("REST request to update branch : ", branches);
        List<BranchDto> result = new ArrayList<>();
        branches.forEach(branchDto -> result.add(branchService.update(branchDto)));
        return ResponseEntity.ok().body(result);
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Get branch by id")
    public ResponseEntity<BranchDto> getBranch(@PathVariable Long id) {
        log.debug("REST request to find branch : ", id);
        BranchDto branch = branchService.findOne(id);
        return ResponseEntity.ok().body(branch);
    }

    @CrossOrigin
    @GetMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get all Branches")
    public ResponseEntity<Page<BranchDto>> findAll(Pageable pageable) {
        log.debug("REST request to find all branches : ");
        Page<BranchDto> branchDtos = branchService.getAll(pageable);
        return ResponseEntity.ok().body(branchDtos);
    }

    @CrossOrigin
    @GetMapping("/getAllBranch")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA','BA','BU','RU')")
    @Operation(summary = "Get all Branches")
    public ResponseEntity<List<BranchListDto>> getAllBranchList() {
        log.debug("REST request to get branch list : ");
        return ResponseEntity.ok().body(branchService.getBranchList());
    }

    @CrossOrigin
    @GetMapping("/getAssignedBranch")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('DOPU','DOEU')")
    @Operation(summary = "Get all AssignedBranch")
    public ResponseEntity<List<AssignedBranchDto>> getAssignedBranch() {
        log.debug("REST request to get Assigned Branch list : ");
        return ResponseEntity.ok().body(branchService.getAssignedBranch());
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Delete branch by id")
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete branch : ", id);
        branchService.delete(id);
    }

    @CrossOrigin
    @GetMapping("/profile/{profileId}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Get user branches ")
    public ResponseEntity<List<BranchDto>> getProfileBranches(@PathVariable Long profileId) {
        log.debug("REST request to get user branches : {}");
        List<BranchDto> dto = branchService.getProfileBranches(profileId);
        return ResponseEntity.ok().body(dto);
    }

    @CrossOrigin
    @DeleteMapping("/users/{branchId}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Delete users from branch")
    void deleteUsers(@PathVariable Long branchId, @RequestBody List<Long> userIds) {
        log.debug("REST request to delete users from branch : {}");
        branchService.deleteUsers(branchId, userIds);
    }

    @CrossOrigin
    @GetMapping("/getSequence")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "get sequence from branch")
    public ResponseEntity<Long> getSequence() {
        log.debug("REST request to get sequence from branch : {}");
        return ResponseEntity.ok().body(branchService.getSequence());
    }

    @CrossOrigin
    @PostMapping("/setPath")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "set Path In Branch")
    void setPathInBranch() {
        log.debug("REST request to set Path In Branch : {}");
        branchService.setPathInBranch();
    }


    @CrossOrigin
    @PostMapping("/getBranchWithProfile")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get user branches ")
    public ResponseEntity<Page<BranchDto>> getBranchWithProfile(@RequestBody AssignedProfileDto assignedProfileDto, Pageable pageable) {
        log.debug("REST request to get user branches : {}");
        Page<BranchDto> dto = branchService.getBranchWithProfile(assignedProfileDto.getBranchId(), assignedProfileDto.getProfileId(), pageable);
        return ResponseEntity.ok().body(dto);
    }

    @CrossOrigin
    @PostMapping("/setBranchPermissions")
    @Operation(summary = "Set branch permissions")
    public ResponseEntity<Void> setBranchPermissions() {
        permissionService.setBranchPermissions();
        return noContent();
    }

    @CrossOrigin
    @GetMapping("/getAllByType")
    @Operation(summary = "get branch list by type")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    public ResponseEntity<List<BranchListDto>> getAllByType(@RequestParam BranchTypeEnum type) {
        log.debug("REST request to get branch list by type: {}");
        return ResponseEntity.ok().body(branchService.getAllByType(type));
    }

}
