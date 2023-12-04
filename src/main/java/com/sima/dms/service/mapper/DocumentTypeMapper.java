package com.sima.dms.service.mapper;


import com.sima.dms.domain.dto.DocumentTypeDto;
import com.sima.dms.domain.entity.DocumentType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentTypeMapper extends EntityMapper<DocumentTypeDto, DocumentType> {


    DocumentTypeDto toDto(DocumentType documentType);

    DocumentType toEntity(DocumentTypeDto documentTypeDto);



    default DocumentType fromId(Long id) {
        if (id == null) {
            return null;
        }
        DocumentType documentType = new DocumentType();
        documentType.setId(id);
        return documentType;
    }
}
