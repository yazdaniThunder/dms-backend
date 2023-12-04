package com.sima.dms.service;

import com.sima.dms.domain.dto.document.DocumentDto;
import com.sima.dms.domain.dto.document.DocumentStateDto;
import com.sima.dms.domain.dto.documentSet.DocumentSetDto;
import com.sima.dms.domain.dto.request.AdvanceDocumentSearchDto;
import com.sima.dms.domain.dto.request.FixConflictDocumentRequestDto;
import com.sima.dms.domain.dto.request.SetConflictDocumentRequestDto;
import com.sima.dms.domain.dto.request.UpdateDocumentRequestDto;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import com.sima.dms.domain.enums.DocumentStateEnum;
import com.sima.dms.domain.enums.WorkflowOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    DocumentDto save(DocumentDto documentDto);

    void update(UpdateDocumentRequestDto updateDocumentRequestDto);

    DocumentDto getById(Long id);

    Page<DocumentDto> getAll(Pageable pageable);

    void delete(Long id);

    void delete(List<Long> ids);

    List<DocumentStateDto> getDocumentHistory(Long documentId);

    Page<DocumentDto> getDocumentsByStatesAndBranchIds(List<DocumentStateEnum> states, Pageable pageable);

    Page<DocumentDto> getByStateAndBranchId(DocumentStateEnum state, Long branchId, Pageable pageable);

    Page<DocumentDto> conflictingManagement(AdvanceDocumentSearchDto searchDto, Pageable pageable);

    DocumentDto complete(Long id, WorkflowOperation operation, String description);

    List<DocumentDto> complete(List<Long> ids, WorkflowOperation operation, String description);

    List<DocumentDto> primaryConfirmDocument(List<Long> documentIds);

    void rescan(Long documentId, MultipartFile content) throws IOException;

    DocumentDto setConflict(SetConflictDocumentRequestDto requestDto);

    DocumentDto fixConflict(FixConflictDocumentRequestDto requestDto);

    void deleteDocumentConflicts(Long documentInfoId);

    Page<DocumentDto> advanceSearch(
            String maintenanceCode,
            List<DocumentStateEnum> states,
            String fromDate,
            String toDate,
            String registerFromDate,
            String registerToDate,
            List<Long> branchIds,
            String filename,
            String documentNumber, String documentDate, String reason, String rowNumber, DocumentSetTypeEnum type,
            String customerNumber,
            String fileNumber,
            Long fileStatusId,
            Long fileTypeId,
            Pageable pageable);

    List<DocumentDto> toStagnateDocument(List<Long> ids);

    List<DocumentDto> toStagnateConflictDocument(List<Long> ids);


    void updateDocument(Long documentSetId, String uuid, MultipartFile content) throws IOException;

    List<DocumentDto> getAllByDocumentState(Long id, DocumentStateEnum state);
}
