package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.document.OtherDocumentDto;
import com.sima.dms.domain.entity.document.OtherDocument;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, BranchMapper.class, FileTypeMapper.class, OtherDocumentStateMapper.class, OtherDocumentFileMapper.class})
public interface OtherDocumentMapper extends EntityMapper<OtherDocumentDto, OtherDocument> {

    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    @Mapping(source = "fileTypeId", target = "fileType")
    OtherDocument toEntity(OtherDocumentDto otherDocumentDto);

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    @Mapping(source = "branch.superVisorName", target = "superVisorCode")
    @Mapping(source = "branch.superVisorCode", target = "superVisorName")
    @Mapping(source = "branch.branchCode", target = "branchCode")
    @Mapping(source = "branch.branchName", target = "branchName")
    OtherDocumentDto toDto(OtherDocument otherDocument);

    @AfterMapping
    default void set(@MappingTarget OtherDocumentDto dto, OtherDocument entity) {
        if (entity.getCreatedBy() != null && entity.getCreatedBy().getUser() != null)
            dto.setCreateByFullName(entity.getCreatedBy().getUser().getFirstName() + " " + entity.getCreatedBy().getUser().getLastName());

        if (entity.getOtherDocumentFiles().stream().anyMatch(otherDocumentFile -> otherDocumentFile.getFile() != null)) {
            dto.setComplete(true);
        }
    }

    default OtherDocument formId(Long id) {
        if (id == null) {
            return null;
        }
        OtherDocument otherDocument = new OtherDocument();
        otherDocument.setId(id);
        return otherDocument;
    }

    default OtherDocumentDto dtoFormId(Long id) {
        if (id == null) {
            return null;
        }
        OtherDocumentDto otherDocumentDto = new OtherDocumentDto();
        otherDocumentDto.setId(id);
        return otherDocumentDto;
    }
}
