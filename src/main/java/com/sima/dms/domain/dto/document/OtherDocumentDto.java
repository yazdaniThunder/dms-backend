package com.sima.dms.domain.dto.document;

import com.sima.dms.domain.dto.baseinformation.FileTypeDto;
import com.sima.dms.domain.dto.common.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(value = "OtherDocumentObject")
public class OtherDocumentDto extends BaseDto {

    private String customerNumber;

    private String fileNumber;

    private Long fileTypeId;

    @Schema(hidden = true)
    private Long superVisorCode;

    @Schema(hidden = true)
    private String superVisorName;

    @Schema(hidden = true)
    private Long branchCode;

    @Schema(hidden = true)
    private String branchName;

    @Schema(hidden = true)
    private Boolean complete = false;

    @Schema(hidden = true)
    private FileTypeDto FileType;

    @Schema(hidden = true)
    private OtherDocumentStateDto lastState;

    @Schema(hidden = true)
    private List<OtherDocumentStateDto> states;

    @Schema(hidden = true)
    private List<OtherDocumentFileDto> otherDocumentFiles;

}
