package com.sima.dms.domain.dto.documentSet;

import com.sima.dms.domain.dto.BranchDto;
import com.sima.dms.domain.dto.common.BaseDto;
import com.sima.dms.domain.dto.document.DocumentDto;
import com.sima.dms.domain.enums.DocumentSetStateEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(value = "DocumentSetObject")
public class DocumentSetDto extends BaseDto {

    private Instant fromDate;

    private Instant toDate;

    private DocumentSetTypeEnum type;

    private String fileNumber;

    private String customerNumber;

    private Long fileStatusId;

    @Schema(hidden = true)
    private String fileStatusTitle;

    @Schema(hidden = true)
    private String fileTypeTitle;

    @Schema(hidden = true)
    private String rowsNumber;

    private String sequence;

    @Schema(hidden = true)
    private Instant sendDate;

    @Schema(hidden = true)
    private String registrarName;

    @Schema(hidden = true)
    private String confirmerName;

    @Schema(hidden = true)
    private String scannerName;

    @Schema(hidden = true)
    private Boolean ocr;

    @Schema(hidden = true)
    private Boolean haveConflict;

    private String description;

    @Schema(hidden = true)
    private Instant primaryConfirmedDate;

    @Schema(hidden = true)
    private Instant conflictingDate;

    @Schema(hidden = true)
    private Instant fixConflictDate;

    @Schema(hidden = true)
    private BranchDto branch;

    @Schema(hidden = true)
    private DocumentSetStateDto state;

    @Schema(hidden = true)
    private DocumentSetStateEnum currentState;

    @Schema(hidden = true)
    private Long documentSize;

    @Schema(hidden = true)
    private Long documentNotCheckedSize;

    @Schema(hidden = true)
    private List<DocumentDto> documents;

    @Schema(hidden = true)
    private List<DocumentSetConflictDto> conflicts;
}
