package com.sima.dms.controller;

import com.sima.dms.domain.dto.documentSet.DocumentSetDto;
import com.sima.dms.domain.dto.request.*;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.enums.DocumentSetStateEnum;
import com.sima.dms.domain.enums.WorkflowOperation;
import com.sima.dms.service.DocumentSetService;
import com.sima.dms.service.ProfileService;
import com.sima.dms.tools.ExcelGenerator;
import com.sima.dms.utils.Responses;
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
import java.util.Date;
import java.util.List;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;
import static com.sima.dms.domain.enums.DocumentSetStateEnum.*;

@RestController
@AllArgsConstructor
@Tag(name = "DocumentSets")
@RequestMapping("/dms/documentSet")
public class DocumentSetController {

    private final ProfileService profileService;
    private final DocumentSetService documentSetService;
    private final ExcelGenerator excelGenerator;
    private final Logger log = LoggerFactory.getLogger(DocumentSetController.class);

    /**
     * Branch services ...
     */

    @CrossOrigin
    @PostMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Create DocumentSet")
    public ResponseEntity<DocumentSetDto> create(@RequestBody DocumentSetDto documentSetDto) {
        log.debug("REST request to create DocumentSet : ", documentSetDto);
        DocumentSetDto dto = documentSetService.save(documentSetDto);
        return ResponseEntity.ok().body(dto);
    }

    @CrossOrigin
    @GetMapping("/registered")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU')")
    @Operation(summary = "Get registered DocumentSets")
    public ResponseEntity<Page<DocumentSetDto>> getRegisteredDocumentSets(Pageable pageable) {
        log.debug("REST request to get registered DocumentSets");
        List<DocumentSetStateEnum> states = Arrays.asList(new DocumentSetStateEnum[]{REGISTERED, REJECTED});
        Page<DocumentSetDto> documentSets = documentSetService.getDocumentSetsByStatesAndBranchId(states, getBranchId(), pageable);
        return ResponseEntity.ok(documentSets);
    }

    @CrossOrigin
    @GetMapping("/acceptWaiting")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Get accept waiting DocumentSets")
    public ResponseEntity<Page<DocumentSetDto>> getAcceptWaitingDocumentSets(Pageable pageable) {
        log.debug("REST request to get accept waiting DocumentSets");
        List<DocumentSetStateEnum> states = Arrays.asList(new DocumentSetStateEnum[]{REGISTERED, FIX_CONFLICT});
        Page<DocumentSetDto> documentSets = documentSetService.getDocumentSetsByStatesAndBranchId(states, getBranchId(), pageable);
        return ResponseEntity.ok(documentSets);
    }

    @CrossOrigin
    @GetMapping("/conflicting")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Get conflicting DocumentSets")
    public ResponseEntity<Page<DocumentSetDto>> getConflictingDocumentSets(Pageable pageable) {
        log.debug("REST request to get conflicting DocumentSets");
        List<DocumentSetStateEnum> states = Arrays.asList(new DocumentSetStateEnum[]{CONFLICTING});
        Page<DocumentSetDto> documentSets = documentSetService.getDocumentSetsByStatesAndBranchId(states, getBranchId(), pageable);
        return ResponseEntity.ok(documentSets);
    }

    @CrossOrigin
    @PutMapping("/{documentSetId}/confirm")
    @Operation(summary = "Confirm DocumentSet")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA','BU')")
    public ResponseEntity<DocumentSetDto> branchConfirmDocumentSet(@PathVariable Long documentSetId) {
        log.debug("REST request to confirm DocumentSet : ");
        DocumentSetDto documentSetDto = documentSetService.branchConfirmDocumentSet(documentSetId);
        return ResponseEntity.ok().body(documentSetDto);
    }

    @CrossOrigin
    @PostMapping("/fixConflict")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA','BU')")
    @Operation(summary = "Fix DocumentSet conflict")
    public ResponseEntity<DocumentSetDto> fixConflict(@RequestBody FixConflictRequestDto requestDto) {
        log.debug("REST request to Fix DocumentSet conflict : ");
        DocumentSetDto dto = documentSetService.fixConflict(requestDto);
        return ResponseEntity.ok().body(dto);
    }

    @CrossOrigin
    @GetMapping("/documentSetByBranch")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Get DocumentSet by branch")
    public ResponseEntity<Page<DocumentSetDto>> getDocumentSetByBranch(Pageable pageable) {
        log.debug("REST request to find DocumentSet by branch : ");
        Page<DocumentSetDto> documentSets = documentSetService.getDocumentSetByBranch(pageable);
        return ResponseEntity.ok().body(documentSets);
    }

    /**
     * Document office services ...
     */

    @CrossOrigin
    @GetMapping("/branchConfirmed")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOPU','DOA')")
    @Operation(summary = "Get branch confirmed DocumentSets")
    public ResponseEntity<Page<DocumentSetDto>> getBranchConfirmDocumentSets(Pageable pageable) {
        log.debug("REST request to get branch confirmed DocumentSets");
        List<DocumentSetStateEnum> states = Arrays.asList(new DocumentSetStateEnum[]{BRANCH_CONFIRMED});
        Page<DocumentSetDto> documentSets = documentSetService.getDocumentSetsByStatesAndBranchIds(states, pageable);
        return ResponseEntity.ok(documentSets);
    }

    @CrossOrigin
    @GetMapping("/primaryConfirmed")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get primary confirmed DocumentSets")
    public ResponseEntity<Page<DocumentSetDto>> getPrimaryConfirmedDocumentSets(Pageable pageable) {
        log.debug("REST request to get primary confirmed DocumentSets");
        List<DocumentSetStateEnum> states = Arrays.asList(new DocumentSetStateEnum[]{PRIMARY_CONFIRMED});
        Page<DocumentSetDto> documentSets = documentSetService.getDocumentSetsByStatesAndBranchIds(states, pageable);
        return ResponseEntity.ok(documentSets);
    }

    @CrossOrigin
    @GetMapping("/scanned")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get scanned DocumentSets")
    public ResponseEntity<Page<DocumentSetDto>> getScannedDocumentSets(Pageable pageable) {
        log.debug("REST request to get scanned DocumentSets");
        List<DocumentSetStateEnum> states = Arrays.asList(new DocumentSetStateEnum[]{SCANNED});
        Page<DocumentSetDto> documentSets = documentSetService.getDocumentSetsByStatesAndBranchIds(states, pageable);
        return ResponseEntity.ok(documentSets);
    }

    @CrossOrigin
    @GetMapping("/processed")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get processed DocumentSets")
    public ResponseEntity<Page<DocumentSetDto>> getProcessedDocumentSets(Pageable pageable) {
        log.debug("REST request to get processed DocumentSets");
        List<DocumentSetStateEnum> states = Arrays.asList(new DocumentSetStateEnum[]{PROCESSED});
        Page<DocumentSetDto> documentSets = documentSetService.getDocumentSetsByStatesAndBranchIds(states, pageable);
        return ResponseEntity.ok(documentSets);
    }

    @CrossOrigin
    @PostMapping("/scan")
    @Operation(summary = "Scan process")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOEU','DOA')")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResponseEntity<DocumentSetDto> scanProcess(@FormDataParam("documentSetId") Long documentSetId, @FormDataParam("file") MultipartFile file) throws IOException {
        log.debug("REST request to scan documents : ");
        DocumentSetDto documentSetDto = documentSetService.scanProcess(documentSetId, file);
        IOUtils.closeQuietly(file.getInputStream());
        return ResponseEntity.ok().body(documentSetDto);
    }

    @CrossOrigin
    @GetMapping("/scanManagement")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOEU','DOA')")
    @Operation(summary = "Get scanned DocumentSets")
    public ResponseEntity<Page<DocumentSetDto>> getPrimaryConfirmedAndScannedDocumentSets(Pageable pageable) {
        log.debug("REST request to get primary confirmed and scanned DocumentSets");
        List<DocumentSetStateEnum> states = Arrays.asList(new DocumentSetStateEnum[]{PRIMARY_CONFIRMED, SCANNED, PROCESSED});
        Page<DocumentSetDto> documentSets = documentSetService.getDocumentSetsByStatesAndBranchIds(states, pageable);
        return ResponseEntity.ok(documentSets);
    }

    @CrossOrigin
    @PutMapping("/{documentSetId}/rescan")
    @Operation(summary = "Rescan DocumentSet")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOEU','DOA')")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResponseEntity<DocumentSetDto> rescan(@PathVariable Long documentSetId) throws IOException {
        log.debug("REST request to rescan DocumentSet : ");
        DocumentSetDto dto = documentSetService.rescan(documentSetId);
        return ResponseEntity.ok().body(dto);
    }

    @CrossOrigin
    @PostMapping("/setConflict")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOPU','DOA')")
    @Operation(summary = "Set conflict to DocumentSet ")
    public ResponseEntity<DocumentSetDto> setConflict(@RequestBody SetConflictRequestDto requestDto) {
        log.debug("REST request to set conflict to  DocumentSet  : ");
        DocumentSetDto dto = documentSetService.setConflict(requestDto);
        return ResponseEntity.ok().body(dto);
    }

    @CrossOrigin
    @PostMapping("/setAllConflict")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOPU','DOA')")
    @Operation(summary = "Set all conflict to DocumentSet ")
    public ResponseEntity<List<DocumentSetDto>> setConflict(@RequestBody SetAllConflictRequestDto requestDto) {
        log.debug("REST request to set all conflict to  DocumentSet  : ");
        List<DocumentSetDto> dto = documentSetService.setAllConflict(requestDto);
        return ResponseEntity.ok().body(dto);
    }

    /**
     * Common services ...
     */

    @CrossOrigin
    @PutMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Update DocumentSet")
    public ResponseEntity<DocumentSetDto> update(@RequestBody DocumentSetDto documentSetDto) {
        log.debug("REST request to update DocumentSet : ", documentSetDto);
        DocumentSetDto dto = documentSetService.update(documentSetDto);
        return ResponseEntity.ok().body(dto);
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOPU','DOEU','DOA')")
    @Operation(summary = "Get DocumentSet by id")
    public ResponseEntity<DocumentSetDto> getDocumentSet(@PathVariable Long id) {
        log.debug("REST request to find DocumentSet : ", id);
        DocumentSetDto dto = documentSetService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @CrossOrigin
    @GetMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA','BU')")
    @Operation(summary = "Get all DocumentSet")
    public ResponseEntity<Page<DocumentSetDto>> findAll(Pageable pageable) {
        log.debug("REST request to find all DocumentSet : ");
        Page<DocumentSetDto> documentSets = documentSetService.findAll(pageable);
        return ResponseEntity.ok().body(documentSets);
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Delete DocumentSet by id")
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete DocumentSet : ", id);
        documentSetService.delete(id);
    }

    @CrossOrigin
    @DeleteMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA')")
    @Operation(summary = "Delete group of DocumentSets by ids")
    public void delete(@RequestBody List<Long> id) {
        log.debug("REST request to delete group of DocumentSet : ", id);
        documentSetService.delete(id);
    }

    @CrossOrigin
    @PutMapping("/{documentSetId}/complete")
    @Operation(summary = "Complete DocumentSet")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOA')")
    public ResponseEntity<DocumentSetDto> complete(@PathVariable Long documentSetId, @RequestParam WorkflowOperation operation, @RequestParam(required = false) String description) {
        log.debug("REST request to complete DocumentSets");
        DocumentSetDto documentSets = documentSetService.complete(documentSetId, operation, description);
        return ResponseEntity.ok(documentSets);
    }

    @CrossOrigin
    @PutMapping("/complete")
    @Operation(summary = "Complete DocumentSets")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','DOPU','BA','DOA')")
    public ResponseEntity<List<DocumentSetDto>> complete(@RequestBody CompleteRequestDto request) {
        log.debug("REST request to complete DocumentSets");
        List<DocumentSetDto> documentSets = documentSetService.complete(request.getIds(), request.getOperation(), request.getDescription());
        return ResponseEntity.ok(documentSets);
    }

//    @CrossOrigin
//    @GetMapping("/{documentSetId}/history")
//    @SecurityRequirement(name = "token")
//    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOU','DOA')")
//    @Operation(summary = "Get DocumentSet history")
//    public ResponseEntity<List<DocumentSetStateDto>> getHistory(@PathVariable Long documentSetId) {
//        log.debug("REST request to get document set history ");
//        List<DocumentSetStateDto> history = documentSetService.getDocumentSetHistory(documentSetId);
//        return ResponseEntity.ok(history);
//    }

    @CrossOrigin
    @PostMapping("/advanceSearch")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Advance search on DocumentSets")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','DOPU','DOEU','DOA')")
    public ResponseEntity<Page<DocumentSetDto>> advanceSearch(@RequestBody AdvanceDocumentSetSearchDto searchDto, Pageable pageable) {
        log.debug("REST request advance search on rules : {}");
        Page<DocumentSetDto> documentSets = documentSetService.advanceSearch(
                searchDto.getType(),
                searchDto.getStatus(),
                searchDto.getFromDate(),
                searchDto.getToDate(),
                searchDto.getRegisterFromDate(),
                searchDto.getRegisterToDate(),
                searchDto.getSentFromDate(),
                searchDto.getSentToDate(),
                searchDto.getRegistrarId(),
                searchDto.getConfirmerId(),
                searchDto.getScannerId(),
                searchDto.getRowNumber(),
                searchDto.getBranchIds(),
                searchDto.getReason(),
                searchDto.getCustomerNumber(),
                searchDto.getFileNumber(),
                searchDto.getFileStatusId(),
                searchDto.getFileTypeId(),
                pageable);
        return ResponseEntity.ok().body(documentSets);
    }

//    @CrossOrigin
//    @GetMapping("/ocrReport")
//    @SecurityRequirement(name = "token")
//    @Operation(summary = "report on DocumentSets")
//    public ResponseEntity<List<OcrDocumentReportDto>> advanceSearch(@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate) {
//        log.debug("REST request advance search on rules : {}");
//        List<OcrDocumentReportDto> documentSets = documentSetService.report(fromDate, toDate);
//        return ResponseEntity.ok().body(documentSets);
//    }

    @CrossOrigin
    @PostMapping("/excel")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Export DocumentSets excel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void exportIntoExcel(@RequestBody AdvanceDocumentSetSearchDto searchDto, HttpServletResponse response) throws IOException {
        log.debug("REST request to export DocumentSets excel : {}");
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=records_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        excelGenerator.generate(searchDto, response);
    }

    @CrossOrigin
    @PostMapping("/reportExcel")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','RU','DOPU')")
    @Operation(summary = "Export report DocumentSets excel")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void report(@RequestBody AdvanceDocumentSetSearchDto searchDto, HttpServletResponse response) throws IOException {
        log.debug("REST request to export report DocumentSets excel : {}");
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=records_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        excelGenerator.generateReport(searchDto, response);
    }


    @CrossOrigin
    @PutMapping("/uploadFile")
    @Operation(summary = "Upload File")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOEU','DOA')")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResponseEntity<DocumentSetDto> uploadFile(@FormDataParam("documentSetId") Long documentSetId, @FormDataParam("file") MultipartFile file) throws IOException {
        log.debug("REST request to upload file : ");
        DocumentSetDto DocumentSetDto = documentSetService.uploadFile(documentSetId, file);
        IOUtils.closeQuietly(file.getInputStream());

        return Responses.ok(DocumentSetDto);
    }

    private Long getBranchId() {
        Profile profile = profileService.findByCurrentId(currentUser().getId());
        return profile.getBranch().getId();
    }
}



