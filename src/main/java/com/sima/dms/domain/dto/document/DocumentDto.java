package com.sima.dms.domain.dto.document;

import com.sima.dms.domain.dto.NodeDocumentDto;
import com.sima.dms.domain.dto.common.BaseDto;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import com.sima.dms.domain.enums.DocumentStateEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;
import java.time.Instant;
import java.util.List;

@Data
@ApiModel(value = "DocumentInfoObject")
public class DocumentDto extends BaseDto {

    private String name;

    private Long documentSetId;

    private String fileUuid;

    @Schema(hidden = true)
    private NodeDocumentDto file;

    private String maintenanceCode;

    @Schema(hidden = true)
    private Long primaryApproverId;

    @Schema(hidden = true)
    private String primaryApproverName;

    @Schema(hidden = true)
    private DocumentStateEnum currentState;

    @Schema(hidden = true)
    private DocumentStateDto state;

    @Schema(hidden = true)
    private List<DocumentConflictDto> conflicts;

    @Schema(hidden = true)
    private Instant fromDate;

    @Schema(hidden = true)
    private Instant toDate;

    @Schema(hidden = true)
    private Long branchCode;

    @Schema(hidden = true)
    private String branchName;

    @Schema(hidden = true)
    private String documentSetRowsNumber;

    @Schema(hidden = true)
    private DocumentSetTypeEnum type;

    @Schema(hidden = true)
    private Instant sendDate;

    @Schema(hidden = true)
    private Boolean haveConflict;

    @Schema(hidden = true)
    private Long ocrProcessTime;

    @Schema(hidden = true)
    private Long biProcessTime;

    @Schema(hidden = true)
    private String scannerName;

    @Schema(hidden = true)
    private Instant fixConflictDate;

    @Schema(hidden = true)
    private Instant primaryConfirmedDate;

    @Schema(hidden = true)
    private Instant sentConflictDate;

    @Schema(hidden = true)
    private Instant ocrFinishedTime;

    @Schema(hidden = true)
    private String fileNumber;

    @Schema(hidden = true)
    private String customerNumber;

    @Schema(hidden = true)
    private String fileStatusTitle;

    @Schema(hidden = true)
    private String fileTypeTitle;
}
