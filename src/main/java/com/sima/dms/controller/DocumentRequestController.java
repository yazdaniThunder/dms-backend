package com.sima.dms.controller;

import com.sima.dms.domain.dto.SentDocumentRequestDto;
import com.sima.dms.domain.dto.document.DocumentRequestDto;
import com.sima.dms.domain.dto.request.CompleteDocumentRequestDto;
import com.sima.dms.domain.dto.request.RequestSearchParameterDto;
import com.sima.dms.domain.dto.request.UpdateDocumentDto;
import com.sima.dms.domain.enums.DocumentRequestStateEnum;
import com.sima.dms.errors.exceptions.GenericException;
import com.sima.dms.service.DocumentRequestService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sima.dms.utils.Responses.noContent;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "DocumentRequest")
@RequestMapping("/dms/documentRequest")
public class DocumentRequestController {

    private final DocumentRequestService documentRequestService;

    @CrossOrigin
    @PostMapping
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','RU')")
    @Operation(summary = "Create document request")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResponseEntity<DocumentRequestDto> save(@Valid @FormDataParam("documentRequestDto") DocumentRequestDto documentRequestDto, @FormDataParam("file") MultipartFile file) throws IOException {
        log.debug("REST request to create new document request");
        DocumentRequestDto documentRequest = documentRequestService.save(documentRequestDto, file);
        if (file != null && !file.isEmpty())
            IOUtils.closeQuietly(file.getInputStream());
        return ResponseEntity.ok().body(documentRequest);
    }

    @CrossOrigin
    @GetMapping("/{id}")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Get by id ")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','RU','DOPU','DOA')")
    public ResponseEntity<DocumentRequestDto> getDocumentRequest(@PathVariable Long id) {
        log.debug("REST request get  Document Request by id");
        return Responses.ok(documentRequestService.findOne(id));
    }

    @CrossOrigin
    @GetMapping
    @Operation(summary = "Get all")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','RU')")
    public ResponseEntity<Page<DocumentRequestDto>> findAll(Pageable pageable) {
        log.debug("REST request get all of  Document Request by pagination");
        return Responses.ok(documentRequestService.findAll(pageable));
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','RU')")
    @Operation(summary = "Delete by id")
    public void delete(@PathVariable Long id) {
        log.debug("REST request delete  Document Request by id");
        documentRequestService.delete(id);
    }

    @CrossOrigin
    @GetMapping("/branchRequests")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','RU')")
    @Operation(summary = "Get branch requests")
    public ResponseEntity<Page<DocumentRequestDto>> getBranchRequests(Pageable pageable) {
        log.debug("REST request get branch requests");
        return Responses.ok(documentRequestService.getBranchRequests(pageable));
    }

    @CrossOrigin
    @GetMapping("/branchSent")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Operation(summary = "Get branch sent requests")
    public ResponseEntity<Page<DocumentRequestDto>> getBranchConfirmed(Pageable pageable) {
        log.debug("REST request get branch confirmed requests");
        return Responses.ok(documentRequestService.findByState(Arrays.asList(DocumentRequestStateEnum.REGISTERED, DocumentRequestStateEnum.BRANCH_REJECTED), pageable));
    }

    @CrossOrigin
    @PostMapping("/uploadFile")
    @Operation(summary = "Upload File")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOPU','DOA')")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResponseEntity<DocumentRequestDto> uploadFile(@FormDataParam("requestId") Long requestId, @FormDataParam("description") String description, @FormDataParam("files") List<MultipartFile> files) throws IOException {
        log.debug("REST request to upload file : ");
        DocumentRequestDto documentRequestDto = documentRequestService.uploadFile(requestId, description, files);
        files.forEach(file -> {
            try {
                IOUtils.closeQuietly(file.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return Responses.ok(documentRequestDto);
    }

    @CrossOrigin
    @PostMapping("/updateDocument")
    @Operation(summary = "update document")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @SecurityRequirement(name = "token")
    public ResponseEntity<Void> updateDocument(@Valid @FormDataParam("updateDocumentDto") UpdateDocumentDto updateDocumentDto, @FormDataParam("content") MultipartFile content) {
        log.debug("REST request to update Document {} :", updateDocumentDto);
        try {
            documentRequestService.updateDocument(updateDocumentDto.getRequestId(),updateDocumentDto.getUuid(), content);
            IOUtils.closeQuietly(content.getInputStream());
            return noContent();
        } catch (Exception e) {
            throw new GenericException(e);
        }
    }

    @CrossOrigin
    @GetMapping("/receive/{id}")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','RU')")
    @Operation(summary = "Receive document")
    public ResponseEntity<DocumentRequestDto> receiveDocument(@PathVariable Long id) {
        log.debug("REST request document receive ");
        return Responses.ok(documentRequestService.receiveDocument(id));
    }

    @CrossOrigin
    @PutMapping("/branchComplete")
    @Operation(summary = "Branch Complete")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','BA')")
    public ResponseEntity<List<DocumentRequestDto>> branchComplete(@RequestBody CompleteDocumentRequestDto request) {
        log.debug("REST request complete by branch");
        return ResponseEntity.ok(documentRequestService.complete(request.getIds(), request.getOperation(), request.getDescription()));
    }

    @CrossOrigin
    @PutMapping("/officeComplete")
    @Operation(summary = "Office Complete")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    public ResponseEntity<List<DocumentRequestDto>> officeComplete(@RequestBody CompleteDocumentRequestDto request) {
        log.debug("REST request complete by office");
        return ResponseEntity.ok(documentRequestService.complete(request.getIds(), request.getOperation(), request.getDescription()));
    }

    @CrossOrigin
    @PutMapping("/send")
    @Operation(summary = "send document")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    public ResponseEntity<DocumentRequestDto> send(@Valid @RequestBody SentDocumentRequestDto sentDocumentRequestDto) {
        log.debug("REST request send");
        return Responses.ok(documentRequestService.send(sentDocumentRequestDto));
    }

    @CrossOrigin
    @PutMapping("/updateExpiryDate")
    @Operation(summary = "update expiry date")
    @SecurityRequirement(name = "token")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOA')")
    public ResponseEntity<DocumentRequestDto> updateExpiryDate(@Valid @RequestBody SentDocumentRequestDto sentDocumentRequestDto) {
        log.debug("REST request update Expiry Date");
        return Responses.ok(documentRequestService.updateExpiryDate(sentDocumentRequestDto));
    }

    @CrossOrigin
    @PostMapping("/branchSearch")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Search on document request in branch")
    @PreAuthorize("hasAnyAuthority('ADMIN','BU','BA','RU')")
    public ResponseEntity<Page<DocumentRequestDto>> branchRequestSearch(@RequestBody RequestSearchParameterDto searchParameter, Pageable pageable) {
        log.debug("REST request search on document request in branch : {}");
        List<DocumentRequestStateEnum> states = new ArrayList<>();
        if (searchParameter.getState() != null)
            states.add(searchParameter.getState());
        Page<DocumentRequestDto> documentRequests = documentRequestService.branchRequestSearch(
                searchParameter.getRegisterDateFrom(),
                searchParameter.getRegisterDateTo(),
                searchParameter.getDocumentNumber(),
                searchParameter.getDocumentDateFrom(),
                searchParameter.getDocumentDateTo(),
                searchParameter.getCustomerNumber(),
                searchParameter.getDocumentType(),
                states,
                searchParameter.getCreatorId(),
                searchParameter.getConfirmerId(),
                searchParameter.getDocumentBranchIds(),
                searchParameter.getRequestBranchIds(),
                searchParameter.getSentDateFrom(),
                searchParameter.getSentDateTo(), pageable);
        return ResponseEntity.ok().body(documentRequests);
    }

    @CrossOrigin
    @PostMapping("/documentOfficeSearch")
    @SecurityRequirement(name = "token")
    @Operation(summary = "Search on document request in document office")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOPU','DOA')")
    public ResponseEntity<Page<DocumentRequestDto>> documentOfficeSearch(@RequestBody RequestSearchParameterDto searchParameter, Pageable pageable) {
        log.debug("REST request search on document request in document office : {}");
        List<DocumentRequestStateEnum> states = new ArrayList<>();
        if (searchParameter.getState() != null)
            states.add(searchParameter.getState());
        Page<DocumentRequestDto> documentRequests = documentRequestService.documentOfficeSearch(
                searchParameter.getRegisterDateFrom(),
                searchParameter.getRegisterDateTo(),
                searchParameter.getDocumentNumber(),
                searchParameter.getDocumentDateFrom(),
                searchParameter.getDocumentDateTo(),
                searchParameter.getCustomerNumber(),
                searchParameter.getDocumentType(),
                states,
                searchParameter.getCreatorId(),
                searchParameter.getConfirmerId(),
                searchParameter.getDocumentBranchIds(),
                searchParameter.getRequestBranchIds(),
                searchParameter.getSentDateFrom(),
                searchParameter.getSentDateTo(), pageable);
        return ResponseEntity.ok().body(documentRequests);
    }
}
