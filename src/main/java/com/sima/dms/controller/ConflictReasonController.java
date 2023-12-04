package com.sima.dms.controller;

import com.sima.dms.domain.dto.baseinformation.ConflictReasonDto;
import com.sima.dms.domain.dto.request.ConflictReasonSearchRequestDto;
import com.sima.dms.domain.dto.request.UpdateActivationDto;
import com.sima.dms.domain.enums.ConflictTypeEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import com.sima.dms.service.ConflictReasonService;
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
@Tag(name = "ConflictReason")
@RequestMapping("/dms/conflictReason")
public class ConflictReasonController {

    private final ConflictReasonService conflictReasonService;
    private final Logger log = LoggerFactory.getLogger(ConflictReasonController.class);

    @CrossOrigin
    @PostMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Create conflict reason")
    public ResponseEntity<ConflictReasonDto> create(@RequestBody ConflictReasonDto reasonDto) {
        if (reasonDto.getId() != null)
            throw new BadRequestException();
        log.debug("REST request to create conflict reason : ", reasonDto);
        return ok(conflictReasonService.save(reasonDto));
    }

    @CrossOrigin
    @PutMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Update conflict reason")
    public ResponseEntity<ConflictReasonDto> update(@RequestBody ConflictReasonDto reasonDto) {
        log.debug("REST request to create conflict reason : ", reasonDto);
        if (reasonDto.getId() == null)
            throw new BadRequestException();
        return ok(conflictReasonService.update(reasonDto));
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "delete conflict reason")
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete conflict reason by id: ", id);
        conflictReasonService.deleteById(id);
    }

    @CrossOrigin
    @DeleteMapping("/deleteByIds")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "delete conflict reason by Ids")
    public void deleteByIds(@RequestBody List<Long> ids) {
        log.debug("REST request to delete conflict reason by ids: ", ids);
        conflictReasonService.deleteByIds(ids);
    }

    @CrossOrigin
    @PutMapping("/activation")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Update activation")
    public ResponseEntity<Void> updateActivation(@RequestBody UpdateActivationDto request) {
        log.debug("REST request to update activation conflict reason : ", request);
        conflictReasonService.updateActive(request.getReasonIds(), request.isActive());
        return noContent();
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get conflict reason by id")
    public ResponseEntity<ConflictReasonDto> getConflictReason(@PathVariable Long id) {
        log.debug("REST request to get conflict reason : ", id);
        return ok(conflictReasonService.getById(id));
    }

    @CrossOrigin
    @GetMapping("/getByDocumentSetType")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA','DOPU','DOEU')")
    @Operation(summary = "Get conflict reason by documentSet type ")
    public ResponseEntity<List<ConflictReasonDto>> getByDocumentSetType(@RequestParam ConflictTypeEnum type, @RequestParam DocumentSetTypeEnum documentSetType) {
        log.debug("REST request to get conflict reasons : ", type, documentSetType);
        return ok(conflictReasonService.getAllByDocumentSetType(type, documentSetType));
    }

    @CrossOrigin
    @GetMapping("/getAllByType")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA','DOPU','DOEU')")
    @Operation(summary = "Get all Document Conflict Reason ")
    public ResponseEntity<List<ConflictReasonDto>> getAllByType(@RequestParam ConflictTypeEnum type) {
        log.debug("REST request to find all conflict reasons : ");
        return ok(conflictReasonService.getAllByType(type));
    }

    @CrossOrigin
    @GetMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get all conflict reasons")
    public ResponseEntity<Page<ConflictReasonDto>> getAll(Pageable pageable) {
        log.debug("REST request to find all conflict reasons : ");
        return ok(conflictReasonService.getAll(pageable));
    }

    @CrossOrigin
    @PostMapping("/search")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get all conflict reasons")
    public ResponseEntity<Page<ConflictReasonDto>> search(@RequestBody ConflictReasonSearchRequestDto request, Pageable pageable) {
        log.debug("REST request to find all conflict reasons : ");
        return ok(conflictReasonService.search(request.getUserId(), request.getReason(), request.getDocumentSetType(), request.getType(), request.getRegDateFrom(), request.getRegDateTo(), pageable));
    }

}
