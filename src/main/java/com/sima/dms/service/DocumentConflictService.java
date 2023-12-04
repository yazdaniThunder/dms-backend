package com.sima.dms.service;

import com.sima.dms.domain.dto.document.DocumentConflictDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentConflictService {

    DocumentConflictDto save(DocumentConflictDto dto);

    DocumentConflictDto update(DocumentConflictDto dto);

    DocumentConflictDto getById(Long id);

    Page<DocumentConflictDto> getAll(Pageable pageable);


    void delete(Long id);

    DocumentConflictDto getByDocumentId(Long documentId);
}
