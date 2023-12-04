package com.sima.dms.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = " NodeDocumentObject")
public class NodeDocumentDto extends NodeBaseDto {

    private String description;
    private boolean textExtracted;
    private String mimeType;
}
