package com.sima.dms.domain.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(value = "SetMetadataRequestObject")
public class SetMetadataRequestDto {

    private String uuid;
    private List<MetadataDto> metadata;

    public SetMetadataRequestDto(String uuid, List<MetadataDto> metadata) {
        this.uuid = uuid;
        this.metadata = metadata;
    }
}
