package com.sima.dms.service;

import com.sima.dms.domain.dto.baseinformation.ConflictReasonDto;
import com.sima.dms.domain.enums.ConflictTypeEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConflictReasonService {

    ConflictReasonDto save(ConflictReasonDto dto);

    ConflictReasonDto update(ConflictReasonDto dto);

    ConflictReasonDto getById(Long id);

    void deleteById(Long id);

    void deleteByIds(List<Long> ids);

    List<ConflictReasonDto> getAllByDocumentSetType(ConflictTypeEnum type , DocumentSetTypeEnum documentSetType);

    List<ConflictReasonDto> getAllByType(ConflictTypeEnum type);

    Page<ConflictReasonDto> getAll(Pageable pageable);

    void updateActive(List<Long> ids, boolean active);

    Page<ConflictReasonDto> search(Long userId, String reason, DocumentSetTypeEnum documentSetType, ConflictTypeEnum type, String regDateTo, String regDateFrom, Pageable pageable);
}
