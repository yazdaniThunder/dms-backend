package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.baseinformation.DocumentRequestReasonDto;
import com.sima.dms.domain.entity.baseinformation.DocumentRequestReason;
import com.sima.dms.repository.DocumentRequestReasonRepository;
import com.sima.dms.service.DocumentRequestReasonService;
import com.sima.dms.service.mapper.DocumentRequestReasonMapper;
import com.sima.dms.service.mapper.RequestReasonValidationMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sima.dms.utils.Responses.badRequest;
import static com.sima.dms.utils.Responses.notFound;

@Service
@AllArgsConstructor
public class DocumentRequestReasonServiceImpl implements DocumentRequestReasonService {

    private final DocumentRequestReasonRepository documentRequestReasonRepository;
    private final DocumentRequestReasonMapper documentRequestReasonMapper;
    private final RequestReasonValidationMapper requestReasonValidationMapper;
    private final Logger log = LoggerFactory.getLogger(DocumentRequestReasonServiceImpl.class);


    @Override
    public DocumentRequestReasonDto save(DocumentRequestReasonDto dto) {
        log.debug("Request to save Document Request Reason : {}", dto);
        if (dto.getTitle() == null || dto.getTitle().equals(""))
            throw badRequest("title must not be null");
        DocumentRequestReason documentRequestReason = documentRequestReasonMapper.toEntity(dto);
        return documentRequestReasonMapper.toDto(documentRequestReasonRepository.save(documentRequestReason));
    }

    @Override
    public DocumentRequestReasonDto update(DocumentRequestReasonDto dto) {
        log.debug("Request to update Document Request Reason : {}", dto);
        DocumentRequestReason documentRequestReason = documentRequestReasonRepository.findById(dto.getId()).orElseThrow(() -> notFound("Document Request Reason  not found"));
        if (dto.getTitle() != null)
            documentRequestReason.setTitle(dto.getTitle());
        if (dto.getRequestReasonValidations() != null && !dto.getRequestReasonValidations().isEmpty()) {
            documentRequestReason.setRequestReasonValidations(requestReasonValidationMapper.toEntity(dto.getRequestReasonValidations()));
        }
        return documentRequestReasonMapper.toDto(documentRequestReasonRepository.save(documentRequestReason));
    }

    @Override
    public DocumentRequestReasonDto getById(Long id) {
        log.debug("Request to get Document Request Reason : {}", id);
        DocumentRequestReason documentRequestReason = documentRequestReasonRepository.findById(id).orElseThrow(() -> notFound("Document Request Reason  not found"));
        return documentRequestReasonMapper.toDto(documentRequestReason);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Request to delete Document Request Reason by id : {}", id);
        documentRequestReasonRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        log.debug("Request to delete Document Request Reason by ids: {}", ids);
        documentRequestReasonRepository.deleteAllById(ids);
    }

    @Override
    public Page<DocumentRequestReasonDto> getAll(Pageable pageable) {
        log.debug("Request to get all Document Request Reason : {}");
        return documentRequestReasonRepository.findAll(pageable)
                .map(documentRequestReasonMapper::toDto);
    }

    @Override
    public List<DocumentRequestReasonDto> getAll() {
        log.debug("Request to get all Document Request Reason active : {}");
        return documentRequestReasonMapper.toDto(documentRequestReasonRepository.findAllByActiveIsTrue());
    }

    @Override
    public void updateActive(List<Long> ids, boolean active) {
        log.debug("Request to update active Document Request Reason : {}", ids, active);
        documentRequestReasonRepository.updateActive(ids, active);
    }
}
