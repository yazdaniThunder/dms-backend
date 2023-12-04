package com.sima.dms.controller;

import com.sima.dms.domain.dto.document.DocumentConflictDto;
import com.sima.dms.service.DocumentConflictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "DocumentConflicts")
@RequestMapping("/dms/document/conflict")
public class DocumentConflictController {

    private final DocumentConflictService documentConflictService;
    private final Logger log = LoggerFactory.getLogger(DocumentConflictController.class);

    @CrossOrigin
    @PutMapping
    @SecurityRequirement(name = "token")
    @Operation(summary = "Update document conflict")
    public ResponseEntity<DocumentConflictDto> update(@RequestBody DocumentConflictDto dto) {
        log.debug("REST request to update document conflict : ", dto);
        DocumentConflictDto documentConflictDto = documentConflictService.update(dto);
        return ResponseEntity.ok().body(documentConflictDto);
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Delete document conflict by id")
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete document conflict : ", id);
        documentConflictService.delete(id);
    }

    @CrossOrigin
    @GetMapping("/getByDocumentId/{documentId}")
    @SecurityRequirement(name = "token")
    @Operation(summary = "get document conflict by documentId")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<DocumentConflictDto> getByDocumentId(@PathVariable Long documentId) {
        log.debug("REST request to delete document conflict : ", documentId);
        return ResponseEntity.ok().body(documentConflictService.getByDocumentId(documentId));
    }

//    @CrossOrigin
//    @PostMapping
//    @SecurityRequirement(name = "token")
//    @Operation(summary = "Create Document Conflict")
//    public ResponseEntity<DocumentConflictDto> create(@RequestBody DocumentConflictDto dto) {
//        log.debug("REST request to create document conflict : ", dto);
//        DocumentConflictDto documentConflictDto = documentConflictService.save(dto);
//        return ResponseEntity.ok().body(documentConflictDto);
//    }
//
//    @CrossOrigin
//    @GetMapping("/{id}")
//    @SecurityRequirement(name = "token")
//    @Operation(summary = "Get Document conflict by id")
//    public ResponseEntity<DocumentConflictDto> getDocsConflict(@PathVariable Long id) {
//        log.debug("REST request to get document conflict : ", id);
//        DocumentConflictDto dto = documentConflictService.getById(id);
//        return ResponseEntity.ok().body(dto);
//    }
//
//    @GetMapping()
//    @SecurityRequirement(name = "token")
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
//    @Operation(summary = "Get all  Document conflict")
//    public ResponseEntity<Page<DocumentConflictDto>> findAll(Pageable pageable) {
//        log.debug("REST request to find all  Document conflict : ");
//        Page<DocumentConflictDto> documentConflicts = documentConflictService.getAll(pageable);
//        return ResponseEntity.ok().body(documentConflicts);
//    }

}
