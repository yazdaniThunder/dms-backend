package com.sima.dms.service.impl;


import com.sima.dms.domain.dto.baseinformation.ConflictReasonDto;
import com.sima.dms.domain.entity.baseinformation.ConflictReason;
import com.sima.dms.domain.enums.ConflictTypeEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;

import static com.sima.dms.domain.enums.ConflictTypeEnum.DOCUMENT_SET;
import static com.sima.dms.utils.Responses.notFound;

import com.sima.dms.repository.ConflictReasonRepository;
import com.sima.dms.service.ConflictReasonService;
import com.sima.dms.service.mapper.ConflictReasonMapper;
import com.sima.dms.utils.DateUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
//@Transactional
@AllArgsConstructor
public class ConflictReasonServiceImpl implements ConflictReasonService {

    private final ConflictReasonMapper conflictReasonMapper;
    private final ConflictReasonRepository conflictReasonRepository;
    private final Logger log = LoggerFactory.getLogger(ConflictReasonServiceImpl.class);

    @Override
    public ConflictReasonDto save(ConflictReasonDto dto) {
        log.debug("Request to save Docs reasons : {}", dto);
        ConflictReason conflictReason = conflictReasonMapper.toEntity(dto);
//        if (dto.getType().equals(ConflictTypeEnum.DOCUMENT))
//            dto.setDocumentSetType(null);
        return conflictReasonMapper.toDto(conflictReasonRepository.save(conflictReason));
    }

    @Override
    public ConflictReasonDto update(ConflictReasonDto dto) {
        log.debug("Request to update Docs reasons : {}", dto);
        ConflictReason conflictReason = conflictReasonRepository.findById(dto.getId()).orElseThrow(() -> notFound("Conflict Reason  not found"));
        if (dto.getDocumentSetType() != null)
            conflictReason.setDocumentSetType(dto.getDocumentSetType());
        conflictReason.setReason(dto.getReason());
        return conflictReasonMapper.toDto(conflictReasonRepository.save(conflictReason));
    }


    @Override
    public ConflictReasonDto getById(Long id) {
        log.debug("Request to get Conflict Reason : {}", id);
        ConflictReason documentSetConflictReason = conflictReasonRepository.findById(id)
                .orElseThrow(() -> notFound("Conflict Reason  not found"));
        return conflictReasonMapper.toDto(documentSetConflictReason);
    }


    @Override
    public void deleteById(Long id) {
        log.debug("Request to delete ConflictReason by id : {}", id);
        conflictReasonRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        log.debug("Request to delete  ConflictReason by ids: {}", ids);
        conflictReasonRepository.deleteAllById(ids);
    }

    @Override
//    @Transactional(readOnly = true)
    public List<ConflictReasonDto> getAllByDocumentSetType(ConflictTypeEnum type,DocumentSetTypeEnum documentSetType) {
        log.debug("Request to get all Docs reasons by documentSetType :{} {}" ,type, documentSetType);
        return conflictReasonRepository.getAllByTypeAndDocumentSetType(type,documentSetType).stream()
                .map(conflictReasonMapper::toDto).collect(Collectors.toList());
    }

    public List<ConflictReasonDto> getAllByType(ConflictTypeEnum type) {
        log.debug("Request to get all conflict reason by type : {}",type);
        return conflictReasonMapper.toDto(conflictReasonRepository.getAllByType(type));
    }

    @Override
//    @Transactional(readOnly = true)
    public Page<ConflictReasonDto> getAll(Pageable pageable) {
        log.debug("Request to get all Docs reasons : {}");
        return conflictReasonRepository.findAll(pageable)
                .map(conflictReasonMapper::toDto);
    }

    @Override
    public void updateActive(List<Long> ids, boolean active) {
        log.debug("Request to update Docs reasons : {}", ids, active);
        conflictReasonRepository.updateActive(ids, active);
    }

    @Override
    public Page<ConflictReasonDto> search(Long userId, String reason, DocumentSetTypeEnum documentSetType, ConflictTypeEnum type, String regDateFrom, String regDateTo, Pageable pageable) {
        log.debug("Request to search on Docs reasons : {}");
        return conflictReasonRepository.search(userId, reason,
                documentSetType, type,
                regDateFrom != null ? DateUtils.toDate(regDateFrom) : null,
                regDateTo != null ? DateUtils.toDate(regDateTo) : null,
                pageable).map(conflictReasonMapper::toDto);
    }
}
