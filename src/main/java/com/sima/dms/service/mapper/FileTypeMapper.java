package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.baseinformation.FileTypeDto;
import com.sima.dms.domain.entity.baseinformation.FileType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class , OtherDocumentTypeMapper.class,FileStatusMapper.class})
public interface FileTypeMapper  extends EntityMapper<FileTypeDto, FileType>{

    @Mapping(source = "createdById", target = "createdBy")
    @Mapping(source = "lastModifiedById", target = "lastModifiedBy")
    FileType toEntity(FileTypeDto fileTypeDto);

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastModifiedBy.id", target = "lastModifiedById")
    FileTypeDto toDto(FileType fileType);

    @AfterMapping
    default void set(@MappingTarget FileTypeDto dto, FileType entity) {
        if (entity.getCreatedBy() !=null && entity.getCreatedBy().getUser() != null)
            dto.setCreateByFullName(entity.getCreatedBy().getUser().getFirstName() + " " + entity.getCreatedBy().getUser().getLastName());
    }

    default FileType formId(Long id) {
        if (id == null) {
            return null;
        }
        FileType fileType = new FileType();
        fileType.setId(id);
        return fileType;
    }

    default FileTypeDto dtoFormId(Long id) {
        if (id == null) {
            return null;
        }
        FileTypeDto fileTypeDto = new FileTypeDto();
        fileTypeDto.setId(id);
        return fileTypeDto;
    }
}
