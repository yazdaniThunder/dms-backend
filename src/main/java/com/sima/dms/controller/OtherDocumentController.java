package com.sima.dms.controller;


import com.sima.dms.domain.dto.document.OtherDocumentDto;
import com.sima.dms.domain.dto.document.OtherDocumentFileDto;
import com.sima.dms.domain.dto.request.AdvanceOtherDocumentSearchDto;
import com.sima.dms.domain.dto.request.CompleteOtherDocumentDto;
import com.sima.dms.service.impl.OtherDocumentServiceImpl;
import com.sima.dms.utils.Responses;
import com.sun.jersey.multipart.FormDataParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

import static com.sima.dms.utils.Responses.noContent;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "otherDocument")
@RequestMapping("/dms/otherDocument")
public class OtherDocumentController {

    private final OtherDocumentServiceImpl otherDocumentService;

    @CrossOrigin
    @PostMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Create otherDocument")
    public ResponseEntity<OtherDocumentDto> save(@Valid @RequestBody OtherDocumentDto otherDocumentDto) {
        log.debug("REST request to create new otherDocument");
        OtherDocumentDto documentRequest = otherDocumentService.save(otherDocumentDto);
        return ResponseEntity.ok().body(documentRequest);
    }

    @CrossOrigin
    @PutMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "update otherDocument")
    public ResponseEntity<OtherDocumentDto> update(@Valid @RequestBody OtherDocumentDto otherDocumentDto) {
        log.debug("REST request to update otherDocument");
        OtherDocumentDto documentRequest = otherDocumentService.update(otherDocumentDto);
        return ResponseEntity.ok().body(documentRequest);
    }

    @CrossOrigin
    @PutMapping("/updateOtherDocumentFile")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "update otherDocument")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResponseEntity<OtherDocumentDto> updateOtherDocumentFile(@Valid @FormDataParam("otherDocumentFileDto") OtherDocumentFileDto otherDocumentFileDto, @FormDataParam("file") MultipartFile file) throws IOException {
        log.debug("REST request to create new otherDocument");
        OtherDocumentDto documentRequest = otherDocumentService.updateOtherDocumentFile(otherDocumentFileDto, file);
        if (file != null && !file.isEmpty())
            IOUtils.closeQuietly(file.getInputStream());
        return ResponseEntity.ok().body(documentRequest);
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Get by id ")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    public ResponseEntity<OtherDocumentDto> getOtherDocument(@PathVariable Long id) {
        log.debug("REST request get  otherDocument by id {} :", id);
        return Responses.ok(otherDocumentService.findOne(id));
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Delete by id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request delete  otherDocument by id");
        otherDocumentService.delete(id);
        return noContent();
    }

    @CrossOrigin
    @DeleteMapping("/deleteAllByIds")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Delete by id")
    public ResponseEntity<Void> deleteAllByIds(@RequestBody List<Long> ids) {
        log.debug("REST request delete all otherDocument by ids : {}:", ids);
        otherDocumentService.deleteByIds(ids);
        return noContent();
    }

    @CrossOrigin
    @PutMapping("/send/{id}")
    @SecurityRequirement(name = "token")
    @Operation(summary = "send otherDocument")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    public ResponseEntity<OtherDocumentDto> send(@PathVariable Long id) {
        log.debug("REST request send otherDocument {} :", id);
        return Responses.ok(otherDocumentService.send(id));
    }

    @CrossOrigin
    @PutMapping("/complete")
    @Operation(summary = "complete otherDocument")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA')")
    public ResponseEntity<List<OtherDocumentDto>> complete(@RequestBody CompleteOtherDocumentDto request) {
        log.debug("REST request complete otherDocument");
        return ResponseEntity.ok(otherDocumentService.complete(request.getIds(), request.getOperation(), request.getDescription()));
    }

    @CrossOrigin
    @PutMapping("/confirm")
    @Operation(summary = "confirm otherDocument")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA')")
    public ResponseEntity<List<OtherDocumentDto>> confirm(@RequestBody CompleteOtherDocumentDto request) {
        log.debug("REST request confirm otherDocument");
        return ResponseEntity.ok(otherDocumentService.confirm(request.getIds(), request.getOperation(), request.getDescription()));
    }

    @CrossOrigin
    @PostMapping("/advanceSearch")
    @SecurityRequirement(name = "token")
    @Operation(summary = "advance otherDocument Search")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    public ResponseEntity<Page<OtherDocumentDto>> advanceSearch(@RequestBody AdvanceOtherDocumentSearchDto advanceOtherDocumentSearchDto, Pageable pageable) {
        log.debug("REST request advanceSearch otherDocument  {} : ");
        Page<OtherDocumentDto> documentRequests = otherDocumentService.advanceSearch(
                advanceOtherDocumentSearchDto, pageable);
        return ResponseEntity.ok().body(documentRequests);
    }
}
