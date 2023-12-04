package com.sima.dms.service.impl;


import com.sima.dms.domain.dto.document.DocumentConflictDto;
import com.sima.dms.domain.entity.document.DocumentConflict;

import static com.sima.dms.utils.Responses.notFound;

import com.sima.dms.repository.DocumentConflictRepository;
import com.sima.dms.service.DocumentConflictService;
import com.sima.dms.service.mapper.ConflictReasonMapper;
import com.sima.dms.service.mapper.DocumentConflictMapper;
import com.sima.dms.utils.Responses;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@Transactional
@AllArgsConstructor
public class DocumentConflictServiceImpl implements DocumentConflictService {

    private final DocumentConflictMapper documentConflictMapper;
    private final DocumentConflictRepository documentConflictRepository;
    private final ConflictReasonMapper documentConflictReasonMapper;

    private final Logger log = LoggerFactory.getLogger(DocumentConflictServiceImpl.class);

    @Override
    public DocumentConflictDto save(DocumentConflictDto documentConflictDto) {
        log.debug("Request to save Document conflict : {}", documentConflictDto);
        DocumentConflict documentConflict = documentConflictMapper.toEntity(documentConflictDto);
        documentConflict = documentConflictRepository.save(documentConflict);
        return documentConflictMapper.toDto(documentConflict);
    }

    @Override
    public DocumentConflictDto update(DocumentConflictDto documentConflictDto) {
        DocumentConflict documentConflict = documentConflictRepository.findById(documentConflictDto.getId()).
                orElseThrow(() -> Responses.notFound("conflict not found"));
        if (documentConflictDto.getRegisterDescription() != null)
            documentConflict.setRegisterDescription(documentConflictDto.getRegisterDescription());

        if (documentConflictDto.getResolveDescription() != null)
            documentConflict.setResolveDescription(documentConflictDto.getResolveDescription());

        if (documentConflictDto.getConflictReasons() != null)
            documentConflict.setConflictReasons(documentConflictReasonMapper.toEntity(documentConflictDto.getConflictReasons()));
        else documentConflict.setConflictReasons(null);

        documentConflict = documentConflictRepository.save(documentConflict);
        documentConflictDto = documentConflictMapper.toDto(documentConflict);
        log.debug("Request to update Document conflict : {}", documentConflictDto);
        return documentConflictDto;
    }

    @Override
    public DocumentConflictDto getById(Long id) {
        log.debug("Request to get Document conflict : {}", id);
        DocumentConflict documentConflict = documentConflictRepository.findById(id).
                orElseThrow(() -> Responses.notFound("conflict not found"));
        return documentConflictMapper.toDto(documentConflict);
    }

    @Override
//    @Transactional(readOnly = true)
    public Page<DocumentConflictDto> getAll(Pageable pageable) {
        log.debug("Request to get all Document conflict : {}");
        return documentConflictRepository.findAll(pageable)
                .map(documentConflictMapper::toDto);
    }


    @Override
    public void delete(Long id) {
        log.debug("Request to delete Document conflict : {}", id);
        documentConflictRepository.deleteById(id);
    }

    @Override
    public DocumentConflictDto getByDocumentId(Long documentId) {
        log.debug("Request to get Document conflict by id : {}", documentId);
        return documentConflictMapper.toDto(documentConflictRepository.findByDocument_Id(documentId));
    }
}
