package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.document.DocumentConflictDto;
import com.sima.dms.domain.entity.document.DocumentConflict;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ConflictReasonMapper.class, DocumentMapper.class, ProfileMapper.class})
public interface DocumentConflictMapper extends EntityMapper<DocumentConflictDto, DocumentConflict> {

    @Mapping(source = "senderId", target = "sender")
    @Mapping(source = "resolverId", target = "resolver")
    @Mapping(source = "documentId", target = "document")
    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    DocumentConflict toEntity(DocumentConflictDto documentConflictDto);

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "resolver.id", target = "resolverId")
    @Mapping(source = "document.id", target = "documentId")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    DocumentConflictDto toDto(DocumentConflict documentConflict);

    @AfterMapping
    default void setProperties(@MappingTarget DocumentConflictDto documentConflictDto, DocumentConflict documentConflict) {

        if (documentConflict.getCreatedBy().getUser() != null)
            documentConflictDto.setRegistrarName(documentConflict.getCreatedBy().getUser().getFirstName() + " " + documentConflict.getCreatedBy().getUser().getLastName());

        if (documentConflict.getSender() !=null &&  documentConflict.getSender().getUser() != null)
            documentConflictDto.setSenderName(documentConflict.getSender().getUser().getFirstName() + " " + documentConflict.getSender().getUser().getLastName());

        if (documentConflict.getResolver() !=null && documentConflict.getResolver().getUser() != null)
            documentConflictDto.setResolverName(documentConflict.getResolver().getUser().getFirstName() + " " + documentConflict.getResolver().getUser().getLastName());
    }

    default DocumentConflict formId(Long id) {
        if (id == null) {
            return null;
        }
        DocumentConflict conflict = new DocumentConflict();
        conflict.setId(id);
        return conflict;
    }
}
