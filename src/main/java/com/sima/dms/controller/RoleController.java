package com.sima.dms.controller;

import com.sima.dms.domain.dto.RoleDto;
import com.sima.dms.domain.enums.RoleEnum;
import com.sima.dms.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sima.dms.utils.Responses.created;
import static com.sima.dms.utils.Responses.ok;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@Tag(name = "Roles")
@RequestMapping("/dms/role")
@SecurityRequirement(name = "token")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @CrossOrigin
    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Register a new role", description = "Returns the new role")
    public ResponseEntity<RoleDto> save(@Validated @RequestBody RoleDto roleDto) {
        RoleDto role = roleService.create(roleDto);
        return created(role, "api/role", role.getId());
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @Operation(summary = "Returns a role by id")
    public ResponseEntity<RoleDto> findOne(@PathVariable Long id) {
        return ok(roleService.findOne(id));
    }

    @CrossOrigin
    @GetMapping
    @Operation(summary = "Returns a list of roles")
    public ResponseEntity<List<RoleDto>> findAll() {
        return ok(roleService.findAll());
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Delete role")
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

//    @CrossOrigin
//    @GetMapping("/getRole")
//    @Operation(summary = "Returns role by persian title")
//    public ResponseEntity<RoleEnum> getRole(@RequestParam String persianRoleTitle){
//        return ok(roleService.getRole(persianRoleTitle));
//    }
}