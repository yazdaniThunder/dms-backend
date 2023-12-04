package com.sima.dms.service.impl;


import com.sima.dms.domain.dto.documentSet.DocumentSetConflictDto;
import com.sima.dms.domain.entity.documentSet.DocumentSetConflict;
import static com.sima.dms.utils.Responses.notFound;
import com.sima.dms.repository.DocumentSetConflictRepository;
import com.sima.dms.service.DocumentSetConflictService;
import com.sima.dms.service.mapper.ConflictReasonMapper;
import com.sima.dms.service.mapper.DocumentSetConflictMapper;
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
public class DocumentSetConflictServiceImpl implements DocumentSetConflictService {

    private final DocumentSetConflictMapper documentSetConflictMapper;
    private final DocumentSetConflictRepository conflictRepository;
    private final ConflictReasonMapper conflictReasonMapper;
    private final Logger log = LoggerFactory.getLogger(DocumentSetConflictServiceImpl.class);

    @Override
    public DocumentSetConflictDto save(DocumentSetConflictDto documentSetConflictDto) {
        log.debug("Request to save document documentSetConflict : {}", documentSetConflictDto);
        DocumentSetConflict documentSetConflict = conflictRepository.save(documentSetConflictMapper.toEntity(documentSetConflictDto));
        return documentSetConflictMapper.toDto(documentSetConflict);
    }

    @Override
    @Transactional
    public DocumentSetConflictDto update(DocumentSetConflictDto documentSetConflictDto) {

        DocumentSetConflict documentSetConflict = conflictRepository.findById(documentSetConflictDto.getId()).
                orElseThrow(() -> notFound( "documentSetConflict not found"));

        if (documentSetConflictDto.getRegisterDescription() != null)
            documentSetConflict.setRegisterDescription(documentSetConflictDto.getRegisterDescription());

        if (documentSetConflictDto.getResolveDescription() != null)
            documentSetConflict.setResolveDescription(documentSetConflictDto.getResolveDescription());

        if (documentSetConflictDto.getConflictReasons() != null)
            documentSetConflict.setConflictReasons(conflictReasonMapper.toEntity(documentSetConflictDto.getConflictReasons()));
        else documentSetConflict.setConflictReasons(null);

        documentSetConflict = conflictRepository.save(documentSetConflict);
        documentSetConflictDto = documentSetConflictMapper.toDto(documentSetConflict);
        log.debug("Request to update document documentSetConflict : {}", documentSetConflictDto);
        return documentSetConflictDto;
    }

    @Override
    public DocumentSetConflictDto getById(Long id) {
        log.debug("Request to get document documentSetConflict : {}", id);

        DocumentSetConflict documentSetConflict = conflictRepository.findById(id).
                orElseThrow(() -> notFound( "documentSetConflict not found"));
        return documentSetConflictMapper.toDto(documentSetConflict);
    }

    @Override
//    @Transactional(readOnly = true)
    public Page<DocumentSetConflictDto> getAll(Pageable pageable) {
        log.debug("Request to get all document documentSetConflict");
        return conflictRepository.findAll(pageable)
                .map(documentSetConflictMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete document documentSetConflict : {}", id);
        conflictRepository.deleteById(id);
    }
}
