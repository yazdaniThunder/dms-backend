package com.sima.dms.service;

import com.sima.dms.domain.dto.baseinformation.DocumentRequestReasonDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocumentRequestReasonService {

    DocumentRequestReasonDto save(DocumentRequestReasonDto dto);

    DocumentRequestReasonDto update(DocumentRequestReasonDto dto);

    DocumentRequestReasonDto getById(Long id);

    void deleteById(Long id);

    void deleteByIds(List<Long> ids);

    Page<DocumentRequestReasonDto> getAll(Pageable pageable);

    List<DocumentRequestReasonDto> getAll();

    void updateActive(List<Long> ids, boolean active);
}
