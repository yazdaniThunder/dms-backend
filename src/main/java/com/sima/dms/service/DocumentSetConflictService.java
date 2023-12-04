package com.sima.dms.service;

import com.sima.dms.domain.dto.documentSet.DocumentSetConflictDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentSetConflictService {

    DocumentSetConflictDto save(DocumentSetConflictDto dto);

    DocumentSetConflictDto update(DocumentSetConflictDto dto);

    DocumentSetConflictDto getById(Long id);

    Page<DocumentSetConflictDto> getAll(Pageable pageable);

    void delete(Long id);
}
