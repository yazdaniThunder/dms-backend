package com.sima.dms.controller;

import com.sima.dms.domain.dto.UserDto;
import com.sima.dms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sima.dms.utils.Responses.noContent;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@AllArgsConstructor
@Tag(name = "Users")
@RequestMapping("/dms/user")
public class UserController {

    private final UserService service;

    @CrossOrigin
    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Register a new user", description = "Returns the new user")
    public ResponseEntity<UserDto> save(@Validated @RequestBody UserDto userDto) {
        UserDto user = service.create(userDto);
        return ResponseEntity.ok().body(user);
    }

    @CrossOrigin
    @PutMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOA','USER')")
    @Operation(summary = "Update user data")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody @Validated UserDto userDto) {
        UserDto user = service.update(id, userDto);
        return ResponseEntity.ok().body(user);
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Show user info")
    public ResponseEntity<UserDto> findOne(@PathVariable Long id) {
        UserDto user = service.findOne(id);
        return ResponseEntity.ok().body(user);
    }

    @CrossOrigin
    @GetMapping
    @SecurityRequirement(name = "token")
    @Operation(summary = "Returns a list of users")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOA','USER')")
    public ResponseEntity<Page<UserDto>> findAll(Pageable pageable) {
        Page<UserDto> page = service.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }

    @CrossOrigin
    @GetMapping("/getDocumentOfficeUsers")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Returns a list of users")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    public ResponseEntity<List<UserDto>> getDocumentOfficeUsers() {
        List<UserDto> UserDtos = service.getDocumentOfficeUsers();
        return ResponseEntity.ok().body(UserDtos);
    }

    @CrossOrigin
    @GetMapping("/getBranchUsers")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Returns a list of users")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    public ResponseEntity<List<UserDto>> getBranchUsers() {
        List<UserDto> UserDtos = service.getBranchUsers();
        return ResponseEntity.ok().body(UserDtos);
    }

    @CrossOrigin
    @PostMapping("/getRegistrar")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Returns a list of registrars")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOPU','DOEU','DOA','RU')")
    public ResponseEntity<List<UserDto>> getRegistrar() {
        List<UserDto> UserDtos = service.getRegistrar();
        return ResponseEntity.ok().body(UserDtos);
    }

    @CrossOrigin
    @PostMapping("/getConfirmer")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Returns a list of confirmer")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOPU','DOEU','DOA','RU')")
    public ResponseEntity<List<UserDto>> getConfirmer() {
        List<UserDto> UserDtos = service.getConfirmer();
        return ResponseEntity.ok().body(UserDtos);
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Delete user")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


    @CrossOrigin
    @PutMapping("/activeAndDeActiveUser")
    @SecurityRequirement(name = "token")
    @Operation(summary = "active And deActive user")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Void> activeAndDeActiveUser(@RequestParam Long userId, @RequestParam Boolean active) {
        service.activeAndDeActiveUser(userId, active);
        return noContent();
    }

}