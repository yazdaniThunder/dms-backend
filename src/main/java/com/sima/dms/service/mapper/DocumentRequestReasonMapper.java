package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.baseinformation.DocumentRequestReasonDto;
import com.sima.dms.domain.entity.baseinformation.DocumentRequestReason;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, RequestReasonValidationMapper.class})
public interface DocumentRequestReasonMapper extends EntityMapper<DocumentRequestReasonDto, DocumentRequestReason>{

    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    DocumentRequestReason toEntity(DocumentRequestReasonDto documentRequestReasonDto);

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    DocumentRequestReasonDto toDto(DocumentRequestReason documentRequestReason);

    @AfterMapping
    default void set(@MappingTarget DocumentRequestReasonDto dto, DocumentRequestReason entity) {
        if (entity.getCreatedBy() !=null && entity.getCreatedBy().getUser() != null)
            dto.setCreateByFullName(entity.getCreatedBy().getUser().getFirstName() + " " + entity.getCreatedBy().getUser().getLastName());
    }

    default DocumentRequestReason formId(Long id) {
        if (id == null) {
            return null;
        }
        DocumentRequestReason reason = new DocumentRequestReason();
        reason.setId(id);
        return reason;
    }

    default DocumentRequestReasonDto dtoFormId(Long id) {
        if (id == null) {
            return null;
        }
        DocumentRequestReasonDto reason = new DocumentRequestReasonDto();
        reason.setId(id);
        return reason;
    }
}
