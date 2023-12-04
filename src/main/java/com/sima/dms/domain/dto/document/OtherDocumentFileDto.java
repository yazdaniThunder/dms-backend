package com.sima.dms.domain.dto.document;

import com.sima.dms.domain.dto.baseinformation.FileStatusDto;
import com.sima.dms.domain.dto.baseinformation.OtherDocumentTypeDto;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@ApiModel(value = "OtherDocumentFileObject")
public class OtherDocumentFileDto {

    private Long id;

    @NotNull(message = "fileStatus must not be null" )
    private Long fileStatusId;

    @Schema(hidden = true)
    private String fileUuid;

    @Schema(hidden = true)
    private FileStatusDto fileStatus;

    @Schema(hidden = true)
    private OtherDocumentTypeDto otherDocumentType;

}
