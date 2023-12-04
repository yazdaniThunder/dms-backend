package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.baseinformation.OtherDocumentTypeDto;
import com.sima.dms.domain.entity.baseinformation.OtherDocumentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FileTypeMapper.class})
public interface OtherDocumentTypeMapper extends EntityMapper<OtherDocumentTypeDto, OtherDocumentType>{

    OtherDocumentType toEntity(OtherDocumentTypeDto otherDocumentTypeDto);


    @Mapping(source = "fileType.title", target = "fileType")
    OtherDocumentTypeDto toDto(OtherDocumentType otherDocumentType);

    default OtherDocumentType formId(Long id) {
        if (id == null) {
            return null;
        }
        OtherDocumentType otherDocumentType = new OtherDocumentType();
        otherDocumentType.setId(id);
        return otherDocumentType;
    }

    default OtherDocumentTypeDto dtoFormId(Long id) {
        if (id == null) {
            return null;
        }
        OtherDocumentTypeDto otherDocumentTypeDto = new OtherDocumentTypeDto();
        otherDocumentTypeDto.setId(id);
        return otherDocumentTypeDto;
    }
}
