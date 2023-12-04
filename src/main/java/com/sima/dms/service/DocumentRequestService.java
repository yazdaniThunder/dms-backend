package com.sima.dms.service;

import com.sima.dms.domain.dto.SentDocumentRequestDto;
import com.sima.dms.domain.dto.document.DocumentRequestDto;
import com.sima.dms.domain.enums.DocumentRequestStateEnum;
import com.sima.dms.domain.enums.DocumentRequestTypeEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import com.sima.dms.domain.enums.WorkflowOperationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentRequestService {

    DocumentRequestDto save(DocumentRequestDto documentRequestDto, MultipartFile file) throws IOException;

    DocumentRequestDto findOne(Long id);

    Page<DocumentRequestDto> findByState(List<DocumentRequestStateEnum> states, Pageable pageable);

    Page<DocumentRequestDto> findAll(Pageable pageable);

    void delete(Long id);

    Page<DocumentRequestDto> getBranchRequests(Pageable pageable);

    DocumentRequestDto uploadFile(Long requestId ,String description, List<MultipartFile> files) throws IOException;

    DocumentRequestDto receiveDocument(Long id);

    List<DocumentRequestDto> complete(List<Long> documentRequestIds, WorkflowOperationState operation, String description);

    DocumentRequestDto send(SentDocumentRequestDto sentDocumentRequestDto);

    DocumentRequestDto updateExpiryDate(SentDocumentRequestDto sentDocumentRequestDto);

    Page<DocumentRequestDto> branchRequestSearch(
            String registerDateFrom,
            String registerDateTo,
            String documentNumber,
            String documentDateFrom,
            String documentDateTo,
            String customerNumber,
            DocumentRequestTypeEnum documentType,
            List<DocumentRequestStateEnum> states,
            Long creatorId,
            Long confirmerId,
            List<Long> documentBranchIds,
            List<Long> requestBranchIds,
            String sentDateFrom,
            String sentDateTo,
            Pageable pageable
    );

    Page<DocumentRequestDto> documentOfficeSearch(
            String registerDateFrom,
            String registerDateTo,
            String documentNumber,
            String documentDateFrom,
            String documentDateTo,
            String customerNumber,
            DocumentRequestTypeEnum documentType,
            List<DocumentRequestStateEnum> states,
            Long creatorId,
            Long confirmerId,
            List<Long> documentBranchIds,
            List<Long> requestBranchIds,
            String sentDateFrom,
            String sentDateTo,
            Pageable pageable
    );

    void updateDocument(Long requestId, String uuid, MultipartFile file) throws IOException;
}
