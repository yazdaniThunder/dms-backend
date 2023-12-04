package com.sima.dms.controller;

import com.sima.dms.domain.dto.documentSet.DocumentSetConflictDto;

import com.sima.dms.service.DocumentSetConflictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "DocumentSetConflict")
@RequestMapping("/dms/documentSet/conflict")
public class DocumentSetConflictController {

    private final DocumentSetConflictService documentSetConflictService;
    private final Logger log = LoggerFactory.getLogger(DocumentSetConflictService.class);

    @CrossOrigin
    @PutMapping
    @SecurityRequirement(name = "token")
    @Operation(summary = "Update DocumentSet conflict")
    public ResponseEntity<DocumentSetConflictDto> update(@RequestBody DocumentSetConflictDto dto) {
        log.debug("REST request to update DocumentSet conflict : ", dto);
        DocumentSetConflictDto docsConflictDto = documentSetConflictService.update(dto);
        return ResponseEntity.ok().body(docsConflictDto);
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Delete DocumentSet conflict by id")
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete DocumentSet conflict : ", id);
        documentSetConflictService.delete(id);
    }
//    @PostMapping
//    @SecurityRequirement(name = "token")
//    @Operation(summary = "Create DocumentSet Conflict")
//    public ResponseEntity<DocumentSetConflictDto> create(@RequestBody DocumentSetConflictDto dto) {
//        log.debug("REST request to create DocumentSet conflict : ", dto);
//        DocumentSetConflictDto docsConflictDto = documentSetConflictService.save(dto);
//        return ResponseEntity.ok().body(docsConflictDto);
//    }
//
//    @GetMapping("/{id}")
//    @SecurityRequirement(name = "token")
//    @Operation(summary = "Get DocumentSet conflict by id")
//    public ResponseEntity<DocumentSetConflictDto> getDocsConflict(@PathVariable Long id) {
//        log.debug("REST request to get DocumentSet conflict : ", id);
//        DocumentSetConflictDto dto = documentSetConflictService.getById(id);
//        return ResponseEntity.ok().body(dto);
//    }
//    
//    @GetMapping()
//    @Operation(summary = "Get all  Docs conflict")
//    public ResponseEntity<Page<DocumentSetConflictDto>> findAll(Pageable pageable) {
//        log.debug("REST request to find all  Docs conflict : ");
//        Page<DocumentSetConflictDto> documentSetConflicts = documentSetConflictService.getAll(pageable);
//        return ResponseEntity.ok().body(documentSetConflicts);
//    }

}
