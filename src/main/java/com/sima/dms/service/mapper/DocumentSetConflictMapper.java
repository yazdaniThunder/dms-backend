package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.documentSet.DocumentSetConflictDto;
import com.sima.dms.domain.entity.documentSet.DocumentSetConflict;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ConflictReasonMapper.class, ProfileMapper.class})
public interface DocumentSetConflictMapper extends EntityMapper<DocumentSetConflictDto, DocumentSetConflict> {

    @Mapping(source = "documentSetId", target = "documentSet.id")
    @Mapping(source = "resolverId", target = "resolver")
    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    DocumentSetConflict toEntity(DocumentSetConflictDto documentSetConflictDto);

    @Mapping(source = "documentSet.id", target = "documentSetId")
    @Mapping(source = "resolver.id", target = "resolverId")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    DocumentSetConflictDto toDto(DocumentSetConflict documentSetConflict);

    @AfterMapping
    default void setProperties(@MappingTarget DocumentSetConflictDto documentSetConflictDto, DocumentSetConflict documentSetConflict) {

        if (documentSetConflict.getCreatedBy().getUser() != null)
            documentSetConflictDto.setRegistrarName(documentSetConflict.getCreatedBy().getUser().getFirstName() + " " + documentSetConflict.getCreatedBy().getUser().getLastName());

        if (documentSetConflict.getResolver() != null && documentSetConflict.getResolver().getUser()!=null )
            documentSetConflictDto.setResolverName(documentSetConflict.getResolver().getUser().getFirstName() + " " + documentSetConflict.getResolver().getUser().getLastName());
    }

    default DocumentSetConflict fo0rmId(Long id) {
        if (id == null) {
            return null;
        }
        DocumentSetConflict documentSetConflict = new DocumentSetConflict();
        documentSetConflict.setId(id);
        return documentSetConflict;
    }
}
