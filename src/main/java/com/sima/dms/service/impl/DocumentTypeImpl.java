package com.sima.dms.service.impl;


import com.sima.dms.domain.dto.DocumentTypeDto;
import com.sima.dms.repository.DocumentTypeRepository;
import com.sima.dms.service.DocumentTypeService;
import com.sima.dms.service.GenericCacheHandler;
import com.sima.dms.service.mapper.DocumentTypeMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DocumentTypeImpl implements DocumentTypeService {


    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMapper documentTypeMapper;
    private final GenericCacheHandler genericCacheHandler;
    private final Logger log= LoggerFactory.getLogger(DocumentTypeImpl.class);

    @Override
    public Page<DocumentTypeDto> paging(Pageable pageable) {
        log.debug("Request to get all Document Types");
        return documentTypeRepository.findAll(pageable)
                .map(documentTypeMapper::toDto);
    }

    @Override
    public List<DocumentTypeDto> getAll() {
        log.debug("Request to get all document types");
        return genericCacheHandler.getDocumentTypeList().stream()
                .map(documentTypeMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<DocumentTypeDto> getByTitle(String title) {
        log.debug("Request to get all document types by title");
        return documentTypeRepository.findByTitle(title).stream()
                .map(documentTypeMapper::toDto).collect(Collectors.toList());
    }
}
