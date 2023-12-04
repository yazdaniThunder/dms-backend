package com.sima.dms.controller;

import com.sima.dms.domain.dto.document.DocumentDto;
import com.sima.dms.domain.dto.request.*;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.enums.DocumentStateEnum;
import com.sima.dms.domain.enums.WorkflowOperation;
import com.sima.dms.errors.exceptions.GenericException;
import com.sima.dms.service.DocumentService;
import com.sima.dms.service.ProfileService;
import com.sima.dms.tools.DocumentExcelGenerator;
import com.sun.jersey.multipart.FormDataParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;
import static com.sima.dms.domain.enums.DocumentStateEnum.*;
import static com.sima.dms.utils.Responses.noContent;
import static com.sima.dms.utils.Responses.ok;

@RestController
@AllArgsConstructor
@Tag(name = "Documents")
@RequestMapping("/dms/document")
public class DocumentController {

    private final DocumentService documentService;
    private final ProfileService profileService;
    private final DocumentExcelGenerator documentExcelGenerator;
    private final Logger log = LoggerFactory.getLogger(DocumentController.class);

    @CrossOrigin
    @PostMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Operation(summary = "Create Document")
    public ResponseEntity<DocumentDto> create(@RequestBody DocumentDto documentDto) {
        log.debug("REST request to create document : ", documentDto);
        DocumentDto dto = documentService.save(documentDto);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * Branch services ...
     */

    @CrossOrigin
    @GetMapping("/sentConflictedDocument")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Get sent conflicted Document")
    public ResponseEntity<Page<DocumentDto>> getSentConflictedDocuments(Pageable pageable) {
        log.debug("REST request to get sent conflicted Document : ");
        Page<DocumentDto> document = documentService.getByStateAndBranchId(SENT_CONFLICT, getBranchId(), pageable);
        return ResponseEntity.ok().body(document);
    }

    @CrossOrigin
    @GetMapping("/fixedConflictedDocuments")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Get fixed conflicted Documents")
    public ResponseEntity<Page<DocumentDto>> getFixedConflictedDocuments(Pageable pageable) {
        log.debug("REST request to get fix conflicted Documents : ");
        Page<DocumentDto> document = documentService.getByStateAndBranchId(FIX_CONFLICT, getBranchId(), pageable);
        return ResponseEntity.ok().body(document);
    }

    @CrossOrigin
    @PostMapping("/fixConflict")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Fix Document conflict")
    public ResponseEntity<DocumentDto> fixConflict(@RequestBody FixConflictDocumentRequestDto requestDto) {
        log.debug("REST request to fix Document conflict : ");
        DocumentDto dto = documentService.fixConflict(requestDto);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * Document office services ...
     */

    @CrossOrigin
    @GetMapping("/notCheckedDocuments")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get not checked Documents")
    public ResponseEntity<Page<DocumentDto>> getNotCheckedDocuments(Pageable pageable) {
        log.debug("REST request to get not checked Documents : ");
        List<DocumentStateEnum> states = Arrays.asList(new DocumentStateEnum[]{NOT_CHECKED});
        Page<DocumentDto> document = documentService.getDocumentsByStatesAndBranchIds(states, pageable);
        return ResponseEntity.ok().body(document);
    }

    @CrossOrigin
    @GetMapping("/primaryConfirmed")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get primary confirmed Documents")
    public ResponseEntity<Page<DocumentDto>> getPrimaryConfirmedDocumentSets(Pageable pageable) {
        log.debug("REST request to get primary confirmed Documents : ");
        Page<DocumentDto> document = documentService.getDocumentsByStatesAndBranchIds(Collections.singletonList(PRIMARY_CONFIRMED), pageable);
        return ResponseEntity.ok().body(document);
    }

    @CrossOrigin
    @PutMapping("/toStagnateDocument")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "to Stagnate Document")
    public ResponseEntity<List<DocumentDto>> toStagnateDocument(@RequestBody List<Long> documentIds) {
        log.debug("REST request to primary confirm Document : ", documentIds);
        List<DocumentDto> documents = documentService.toStagnateDocument(documentIds);
        return ResponseEntity.ok().body(documents);
    }

    @CrossOrigin
    @PutMapping("/toStagnateConflictDocument")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "to Stagnate Document")
    public ResponseEntity<List<DocumentDto>> toStagnateConflictDocument(@RequestBody List<Long> documentIds) {
        log.debug("REST request to primary confirm Document : ", documentIds);
        List<DocumentDto> documents = documentService.toStagnateConflictDocument(documentIds);
        return ResponseEntity.ok().body(documents);
    }

    @CrossOrigin
    @GetMapping("/conflictedDocuments")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get conflicted Documents")
    public ResponseEntity<Page<DocumentDto>> getConflictedDocuments(Pageable pageable) {
        log.debug("REST request to get conflicted Documents : ");
        Page<DocumentDto> document = documentService.getDocumentsByStatesAndBranchIds(Collections.singletonList(CONFLICTING), pageable);
        return ResponseEntity.ok().body(document);
    }

    @CrossOrigin
    @PutMapping("/primaryConfirm")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOEU','DOA')")
    @Operation(summary = "Primary confirm Document")
    public ResponseEntity<List<DocumentDto>> primaryConfirmDocument(@RequestBody List<Long> documentIds) {
        log.debug("REST request to primary confirm Document : ", documentIds);
        List<DocumentDto> documents = documentService.primaryConfirmDocument(documentIds);
        return ResponseEntity.ok().body(documents);
    }

    @CrossOrigin
    @PostMapping("/conflictingManagement")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOEU','DOA')")
    @Operation(summary = "Get fixed conflicted Documents")
    public ResponseEntity<Page<DocumentDto>> conflictingManagement(@RequestBody AdvanceDocumentSearchDto searchDto, Pageable pageable) {
        log.debug("REST request to get Documents for conflicting Management : ");
        Page<DocumentDto> document = documentService.conflictingManagement(searchDto, pageable);
        return ResponseEntity.ok().body(document);
    }

    @CrossOrigin
    @PostMapping("/rescan")
    @Operation(summary = "Rescan file")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOEU','DOA')")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResponseEntity<Void> rescan(@FormDataParam("documentSetId") Long documentId, @FormDataParam("file") MultipartFile file) throws IOException {
        log.debug("REST request to rescan file : ");
        documentService.rescan(documentId, file);
        return noContent();
    }

    @CrossOrigin
    @PostMapping("/updateDocument")
    @Operation(summary = "update document")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOEU','DOA')")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @SecurityRequirement(name = "token")
    public ResponseEntity<Void> updateDocument( @FormDataParam("documentSetId") Long documentSetId, @FormDataParam("uuid") String uuid, @FormDataParam("content") MultipartFile content) {
        log.debug("REST request to update Document", uuid);
        try {
            documentService.updateDocument(documentSetId,uuid, content);
            IOUtils.closeQuietly(content.getInputStream());
            return noContent();
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @CrossOrigin
    @PostMapping("/setConflict")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOEU','DOA')")
    @Operation(summary = "Set conflict to Document")
    public ResponseEntity<DocumentDto> setConflict(@RequestBody SetConflictDocumentRequestDto requestDto) {
        log.debug("REST request to set conflict to Document : ");
        DocumentDto dto = documentService.setConflict(requestDto);
        return ResponseEntity.ok().body(dto);
    }

    @CrossOrigin
    @DeleteMapping("/{documentId}/deleteConflicts")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Delete document conflicts by documentId")
    public void deleteDocumentConflicts(@PathVariable Long documentId) {
        log.debug("REST request to delete document conflicts by documentId : ", documentId);
        documentService.deleteDocumentConflicts(documentId);
    }

    @CrossOrigin
    @GetMapping("/primaryConfirmAndConflicted")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get Primary confirm and Conflicted Documents")
    public ResponseEntity<Page<DocumentDto>> getPrimaryAndConflictedDocument(Pageable pageable) {
        log.debug("REST request to get Primary confirm and Conflicted Documents : ");
        List<DocumentStateEnum> states = Arrays.asList(new DocumentStateEnum[]{PRIMARY_CONFIRMED, CONFLICTING});
        Page<DocumentDto> document = documentService.getDocumentsByStatesAndBranchIds(states, pageable);
        return ResponseEntity.ok().body(document);
    }

    @CrossOrigin
    @GetMapping("/getAllByDocumentState")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA','DOEU')")
    @Operation(summary = "Get all Document By Document State")
    public ResponseEntity<List<DocumentDto>> getAllByDocumentState(@RequestParam Long documentSetId , @RequestParam DocumentStateEnum state) {
        log.debug("REST request to get By Document State : ", documentSetId , state);
        List<DocumentDto> documentDtos = documentService.getAllByDocumentState(documentSetId , state);
        return ResponseEntity.ok().body(documentDtos);
    }

    /**
     * Common services ...
     */

    @CrossOrigin
    @PutMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA','DOEU')")
    @Operation(summary = "Update Document")
    public ResponseEntity<Void> update(@RequestBody UpdateDocumentRequestDto requestDto) {
        log.debug("REST request to update document : ", requestDto);
        documentService.update(requestDto);
        return noContent();
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOPU','DOEU','DOA','RU')")
    @Operation(summary = "Get Document by id")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable Long id) {
        log.debug("REST request to get document : ", id);
        DocumentDto documentDto = documentService.getById(id);
        return ResponseEntity.ok().body(documentDto);
    }

    @CrossOrigin
    @GetMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA','BU')")
    @Operation(summary = "Get all Documents")
    public ResponseEntity<Page<DocumentDto>> findAll(Pageable pageable) {
        log.debug("REST request to find all Documents : ");
        Page<DocumentDto> documents = documentService.getAll(pageable);
        return ok(documents);
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA','BU')")
    @Operation(summary = "Delete Document by id")
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Document : ", id);
        documentService.delete(id);
    }

    @CrossOrigin
    @DeleteMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA','BU')")
    @Operation(summary = "Delete group of Documents by ids")
    public void delete(@RequestBody List<Long> ids) {
        log.debug("REST request to delete group of Documents by ids : ", ids);
        documentService.delete(ids);
    }

    @CrossOrigin
    @PutMapping("/{documentId}/complete")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOA')")
    @Operation(summary = "Complete Document")
    public ResponseEntity<DocumentDto> complete(@PathVariable Long documentId, @RequestParam WorkflowOperation operation, @RequestParam(required = false) String description) {
        log.debug("REST request to complete Document");
        DocumentDto documents = documentService.complete(documentId, operation, description);
        return ResponseEntity.ok(documents);
    }

    @CrossOrigin
    @PutMapping("/complete")
    @Operation(summary = "Complete Document")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOA')")
    public ResponseEntity<List<DocumentDto>> complete(@RequestBody CompleteRequestDto request) {
        log.debug("REST request to complete Document");
        List<DocumentDto> documents = documentService.complete(request.getIds(), request.getOperation(), request.getDescription());
        return ResponseEntity.ok(documents);
    }

//    @CrossOrigin
//    @GetMapping("/{documentId}/history")
//    @SecurityRequirement(name = "token")
//    @Operation(summary = "Get Document history")
//    public ResponseEntity<List<DocumentStateDto>> getHistory(@PathVariable Long documentId) {
//        log.debug("REST request to get Document history ");
//        List<DocumentStateDto> documentHistory = documentService.getDocumentHistory(documentId);
//        return ResponseEntity.ok(documentHistory);
//    }

    @CrossOrigin
    @PostMapping("/advanceSearch")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Advance search on Documents")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOPU','DOEU','DOA','RU')")
    public ResponseEntity<Page<DocumentDto>> advanceSearch(@RequestBody AdvanceDocumentSearchDto searchDto, Pageable pageable) {
        log.debug("REST request advance search on documents : {}");
        Page<DocumentDto> documentSets = documentService.advanceSearch(
                searchDto.getMaintenanceCode(),
                searchDto.getStates(),
                searchDto.getFromDate(),
                searchDto.getToDate(),
                searchDto.getRegisterFromDate(),
                searchDto.getRegisterToDate(),
                searchDto.getBranchIds(),
                searchDto.getFilename(),
                searchDto.getDocumentNumber(),
                searchDto.getDocumentDate(),
                searchDto.getReason(),
                searchDto.getRowNumber(),
                searchDto.getType(),
                searchDto.getCustomerNumber(),
                searchDto.getFileNumber(),
                searchDto.getFileStatusId(),
                searchDto.getFileTypeId(),
                pageable);

        return ResponseEntity.ok().body(documentSets);
    }

    private Long getBranchId() {
        Profile profile = profileService.findByCurrentId(currentUser().getId());
        return profile.getBranch().getId();
    }

    @CrossOrigin
    @PostMapping("/excel")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Export Document excel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void exportIntoExcel(@RequestBody AdvanceDocumentSearchDto searchDto, HttpServletResponse response) throws IOException {
        log.debug("REST request to export Document excel : {}");
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=records_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        documentExcelGenerator.generate(searchDto, response);
    }

    @CrossOrigin
    @PostMapping("/reportExcel")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','RU','DOEU')")
    @Operation(summary = "Export Document report excel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void report(@RequestBody AdvanceDocumentSearchDto searchDto, HttpServletResponse response) throws IOException {
        log.debug("REST request to export Document  report excel : {}");
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=records_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        documentExcelGenerator.generateReport(searchDto, response);
    }


}
