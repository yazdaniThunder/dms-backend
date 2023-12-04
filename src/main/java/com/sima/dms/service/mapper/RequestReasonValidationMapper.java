package com.sima.dms.service.mapper;

import com.sima.dms.domain.dto.baseinformation.RequestReasonValidationDto;
import com.sima.dms.domain.entity.baseinformation.RequestReasonValidation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DocumentRequestReasonMapper.class})
public interface RequestReasonValidationMapper extends EntityMapper<RequestReasonValidationDto, RequestReasonValidation>{


    RequestReasonValidation toEntity(RequestReasonValidationDto requestReasonValidationDto);


    RequestReasonValidationDto toDto(RequestReasonValidation requestReasonValidation);

    default RequestReasonValidation formId(Long id) {
        if (id == null) {
            return null;
        }
        RequestReasonValidation requestReasonValidation = new RequestReasonValidation();
        requestReasonValidation.setId(id);
        return requestReasonValidation;
    }

    default RequestReasonValidationDto dtoFormId(Long id) {
        if (id == null) {
            return null;
        }
        RequestReasonValidationDto reasonValidationDto = new RequestReasonValidationDto();
        reasonValidationDto.setId(id);
        return reasonValidationDto;
    }
}
