package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.baseinformation.FileStatusDto;
import com.sima.dms.domain.entity.baseinformation.FileStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FileTypeMapper.class})
public interface FileStatusMapper extends EntityMapper<FileStatusDto, FileStatus> {

    FileStatus toEntity(FileStatusDto fileStatusDto);


    @Mapping(source = "fileType.title", target = "fileType")
    FileStatusDto toDto(FileStatus fileStatus);

    default FileStatus formId(Long id) {
        if (id == null) {
            return null;
        }
        FileStatus fileStatus = new FileStatus();
        fileStatus.setId(id);
        return fileStatus;
    }

    default FileStatusDto dtoFormId(Long id) {
        if (id == null) {
            return null;
        }
        FileStatusDto fileStatusDto = new FileStatusDto();
        fileStatusDto.setId(id);
        return fileStatusDto;
    }
}
