package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.document.OtherDocumentFileDto;
import com.sima.dms.domain.entity.document.OtherDocumentFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {NodeDocumentMapper.class, OtherDocumentTypeMapper.class, FileStatusMapper.class, OtherDocumentMapper.class})
public interface OtherDocumentFileMapper extends EntityMapper<OtherDocumentFileDto, OtherDocumentFile> {

    @Mapping(source = "fileStatusId", target = "fileStatus")
    OtherDocumentFile toEntity(OtherDocumentFileDto otherDocumentFileDto);

    @Mapping(source = "file.uuid", target = "fileUuid")
    OtherDocumentFileDto toDto(OtherDocumentFile otherDocumentFile);


    default OtherDocumentFile formId(Long id) {
        if (id == null) {
            return null;
        }
        OtherDocumentFile otherDocumentFile = new OtherDocumentFile();
        otherDocumentFile.setId(id);
        return otherDocumentFile;
    }

    default OtherDocumentFileDto dtoFormId(Long id) {
        if (id == null) {
            return null;
        }
        OtherDocumentFileDto otherDocumentFileDto = new OtherDocumentFileDto();
        otherDocumentFileDto.setId(id);
        return otherDocumentFileDto;
    }
}
