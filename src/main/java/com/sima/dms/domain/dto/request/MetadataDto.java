package com.sima.dms.domain.dto.request;

import com.sima.dms.domain.enums.MetadataFieldNameEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MetadataDto {

    private MetadataFieldNameEnum name;
    private String value;

    public MetadataDto(MetadataFieldNameEnum name, String value) {
        this.name = name;
        this.value = value;
    }
}
