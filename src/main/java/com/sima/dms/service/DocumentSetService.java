package com.sima.dms.service;

import com.sima.dms.domain.dto.documentSet.DocumentSetDto;
import com.sima.dms.domain.dto.documentSet.DocumentSetStateDto;
import com.sima.dms.domain.dto.OcrDocumentReportDto;
import com.sima.dms.domain.dto.request.FixConflictRequestDto;
import com.sima.dms.domain.dto.request.SetAllConflictRequestDto;
import com.sima.dms.domain.dto.request.SetConflictRequestDto;
import com.sima.dms.domain.enums.DocumentSetStateEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import com.sima.dms.domain.enums.DocumentStateEnum;
import com.sima.dms.domain.enums.WorkflowOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentSetService {

    DocumentSetDto save(DocumentSetDto documentSetDto);

    DocumentSetDto update(DocumentSetDto documentSetDto);

    DocumentSetDto findById(Long id);

    Page<DocumentSetDto> findAll(Pageable pageable);

    Page<DocumentSetDto> getDocumentSetByBranch(Pageable pageable);

    void delete(Long id);

    void delete(List<Long> id);

    List<DocumentSetStateDto> getDocumentSetHistory(Long documentSetId);

    Page<DocumentSetDto> getDocumentSetsByStatesAndBranchIds(List<DocumentSetStateEnum> states, Pageable pageable);

    Page<DocumentSetDto> getDocumentSetsByStatesAndBranchId(List<DocumentSetStateEnum> states, Long branchId, Pageable pageable);

    DocumentSetDto branchConfirmDocumentSet(Long documentSetId);

    DocumentSetDto setConflict(SetConflictRequestDto dto);

    List<DocumentSetDto> setAllConflict(SetAllConflictRequestDto dto);

    DocumentSetDto fixConflict(FixConflictRequestDto dto);

    DocumentSetDto scanProcess(Long documentSetId, MultipartFile content) throws IOException;

    DocumentSetDto rescan(Long documentSetId) throws IOException;

    DocumentSetDto complete(Long documentSetId, WorkflowOperation operation, String description);

    List<DocumentSetDto> complete(List<Long> ids, WorkflowOperation operation, String description);

    Page<DocumentSetDto> advanceSearch(
            DocumentSetTypeEnum type,
            List<DocumentSetStateEnum> status,
            String fromDate,
            String toDate,
            String registerFromDate,
            String registerToDate,
            String sentFromDate,
            String sentToDate,
            Long registrarId,
            Long confirmerId,
            Long scannerId,
            String rowNumber,
            List<Long> branchIds,
            String reason,
            String customerNumber,
            String fileNumber,
            Long fileStatusId,
            Long fileTypeId,
            Pageable pageable);

//    List<OcrDocumentReportDto> report(String registerFromDate,
//                                      String registerToDate);

    DocumentSetDto uploadFile(Long documentSetId, MultipartFile file) throws IOException;

}
