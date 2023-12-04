package com.sima.dms.service;

import com.sima.dms.domain.dto.document.OtherDocumentDto;
import com.sima.dms.domain.dto.document.OtherDocumentFileDto;
import com.sima.dms.domain.dto.request.AdvanceOtherDocumentSearchDto;
import com.sima.dms.domain.enums.WorkflowOperationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OtherDocumentService {

    OtherDocumentDto save(OtherDocumentDto otherDocumentDto);

    OtherDocumentDto updateOtherDocumentFile(OtherDocumentFileDto otherDocumentFileDto, MultipartFile file) throws IOException;
    
    OtherDocumentDto update(OtherDocumentDto otherDocumentDto);

    OtherDocumentDto findOne(Long id);

    OtherDocumentDto send(Long id);

    void delete(Long id);

    void deleteByIds(List<Long> ids);

    List<OtherDocumentDto> confirm(List<Long> otherDocumentIds, WorkflowOperationState operation, String description);

    List<OtherDocumentDto> complete(List<Long> otherDocumentIds, WorkflowOperationState operation, String description);

    Page<OtherDocumentDto> advanceSearch(AdvanceOtherDocumentSearchDto advanceOtherDocumentSearchDto, Pageable pageable);
}
