package com.sima.dms.controller;


import com.sima.dms.domain.dto.DocumentTypeDto;
import com.sima.dms.service.DocumentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "DocumentType")
@RequestMapping("/dms/documentType")
public class DocumentTypeController {

    private DocumentTypeService documentTypeService;
    private final Logger log = LoggerFactory.getLogger(DocumentTypeController.class);

    @GetMapping
    @CrossOrigin
    @Operation(summary = "Returns a list of document types")
    public ResponseEntity<Page<DocumentTypeDto>> getAll(Pageable pageable) {
        log.debug("REST request to find all document types : ");
        Page<DocumentTypeDto> documentTypes = documentTypeService.paging(pageable);
        return ResponseEntity.ok().body(documentTypes);
    }

    @CrossOrigin
    @GetMapping("/getAllDocumentTypes")
    @Operation(summary = "Get all document types")
    public ResponseEntity<List<DocumentTypeDto>> getAllDocumentTypes() {
        log.debug("REST request to get document type list list : ");
        return ResponseEntity.ok().body(documentTypeService.getAll());
    }

    @CrossOrigin
    @GetMapping("/title")
    @Operation(summary = "Get all document types by title")
    public ResponseEntity<List<DocumentTypeDto>> getAllByDocumentClass(String title) {
        log.debug("REST request to getDocumentType by title : ");
        return ResponseEntity.ok().body(documentTypeService.getByTitle(title));


    }

}
