package com.sima.dms.controller;

import com.sima.dms.domain.dto.baseinformation.DocumentRequestReasonDto;
import com.sima.dms.domain.dto.request.UpdateActivationDto;
import com.sima.dms.service.DocumentRequestReasonService;
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
@Tag(name = "DocumentRequestReason")
@RequestMapping("/dms/documentRequestReason")
public class DocumentRequestReasonController {

    private final DocumentRequestReasonService documentRequestReasonService;
    private final Logger log = LoggerFactory.getLogger(DocumentRequestReasonController.class);

    @CrossOrigin
    @PostMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Create Document Request Reason")
    public ResponseEntity<DocumentRequestReasonDto> create(@RequestBody DocumentRequestReasonDto reasonDto) {
        log.debug("REST request to create Document Request Reason : ", reasonDto);
        return ok(documentRequestReasonService.save(reasonDto));
    }

    @CrossOrigin
    @PutMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Update Document Request Reason")
    public ResponseEntity<DocumentRequestReasonDto> update(@RequestBody DocumentRequestReasonDto reasonDto) {
        log.debug("REST request to update Document Request Reason : ", reasonDto);
        if (reasonDto.getId() == null)
            throw new BadRequestException();
        return ok(documentRequestReasonService.update(reasonDto));
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "delete Document Request Reason")
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Document Request Reason by id: ", id);
        documentRequestReasonService.deleteById(id);
    }

    @CrossOrigin
    @DeleteMapping("/deleteByIds")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "delete Document Request Reason by Ids")
    public void deleteByIds(@RequestBody List<Long> ids) {
        log.debug("REST request to delete Document Request Reason by ids: ", ids);
        documentRequestReasonService.deleteByIds(ids);
    }

    @CrossOrigin
    @PutMapping("/activation")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Update activation")
    public ResponseEntity<Void> updateActivation(@RequestBody UpdateActivationDto request) {
        log.debug("REST request to update activation Document Request Reason : ", request);
        documentRequestReasonService.updateActive(request.getReasonIds(), request.isActive());
        return noContent();
    }
    @CrossOrigin
    @GetMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get Document Request Reason by id")
    public ResponseEntity<DocumentRequestReasonDto> getById(@PathVariable Long id) {
        log.debug("REST request to get conflict reason : ", id);
        return ok(documentRequestReasonService.getById(id));
    }

    @CrossOrigin
    @GetMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get all Document Request Reason")
    public ResponseEntity<Page<DocumentRequestReasonDto>> getAll(Pageable pageable) {
        log.debug("REST request to find all Document Request Reason : ");
        return ok(documentRequestReasonService.getAll(pageable));
    }

    @CrossOrigin
    @GetMapping("/getAllList")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA','BA','BU','RU')")
    @Operation(summary = "Get all Document Request Reason ")
    public ResponseEntity<List<DocumentRequestReasonDto>> getAllList(){
        log.debug("REST request to find all Document Request Reason : ");
        return ok(documentRequestReasonService.getAll());
    }
}
