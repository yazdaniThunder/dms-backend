package com.sima.dms.controller;

import com.sima.dms.domain.dto.baseinformation.FileTypeDto;
import com.sima.dms.domain.dto.request.UpdateActivationDto;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import com.sima.dms.service.FileTypeService;
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

import javax.ws.rs.BadRequestException;
import java.util.List;

import static com.sima.dms.utils.Responses.noContent;
import static com.sima.dms.utils.Responses.ok;

@RestController
@AllArgsConstructor
@Tag(name = "FileType")
@RequestMapping("/dms/fileType")
public class FileTypeController {

    private final FileTypeService fileTypeService;

    private final Logger log = LoggerFactory.getLogger(FileTypeController.class);

    @CrossOrigin
    @PostMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Create file type")
    public ResponseEntity<FileTypeDto> create(@RequestBody FileTypeDto fileTypeDto) {
        if (fileTypeDto.getId() != null)
            throw new BadRequestException();
        log.debug("REST request to create file type : ", fileTypeDto);
        return ok(fileTypeService.save(fileTypeDto));
    }

    @CrossOrigin
    @PutMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Update file type")
    public ResponseEntity<FileTypeDto> update(@RequestBody FileTypeDto fileTypeDto) {
        log.debug("REST request to update file type : ", fileTypeDto);
        if (fileTypeDto.getId() == null)
            throw new BadRequestException();
        return ok(fileTypeService.update(fileTypeDto));
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "delete file type")
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete file type by id: ", id);
        fileTypeService.deleteById(id);
    }

    @CrossOrigin
    @DeleteMapping("/deleteByIds")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "delete file type by Ids")
    public void deleteByIds(@RequestBody List<Long> ids) {
        log.debug("REST request to delete file type by ids: ", ids);
        fileTypeService.deleteByIds(ids);
    }

    @CrossOrigin
    @PutMapping("/activation")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Update activation")
    public ResponseEntity<Void> updateActivation(@RequestBody UpdateActivationDto request) {
        log.debug("REST request to update activation conflict reason : ", request);
        fileTypeService.updateActive(request.getReasonIds(), request.isActive());
        return noContent();
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get file type by id")
    public ResponseEntity<FileTypeDto> getFileType(@PathVariable Long id) {
        log.debug("REST request to get file type : ", id);
        return ok(fileTypeService.getById(id));
    }

    @CrossOrigin
    @GetMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get all file type")
    public ResponseEntity<Page<FileTypeDto>> getAll(Pageable pageable) {
        log.debug("REST request to find all file type : ");
        return ok(fileTypeService.getAll(pageable));
    }

    @CrossOrigin
    @GetMapping("/getList")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA','BU','BA')")
    @Operation(summary = "Get all List file type")
    public ResponseEntity<List<FileTypeDto>> getList() {
        log.debug("REST request to find all List file type : ");
        return ok(fileTypeService.getList());
    }

    @CrossOrigin
    @GetMapping("/getByTitle")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Get file type by title")
    public ResponseEntity<FileTypeDto> getByTitle(@RequestParam DocumentSetTypeEnum title) {
        log.debug("REST request to get file type by title {} : ", title);
        return ok(fileTypeService.getByTitle(title));
    }
}
