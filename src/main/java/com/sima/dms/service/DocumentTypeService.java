package com.sima.dms.service;

import com.sima.dms.domain.dto.DocumentTypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocumentTypeService {

    Page<DocumentTypeDto>paging(Pageable pageable);
    List<DocumentTypeDto> getAll();

    List<DocumentTypeDto>getByTitle(String title);


}
