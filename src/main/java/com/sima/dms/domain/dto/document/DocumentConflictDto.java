package com.sima.dms.domain.dto.document;

import com.sima.dms.domain.dto.common.BaseDto;
import com.sima.dms.domain.dto.baseinformation.ConflictReasonDto;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@ApiModel(value = " DocumentConflictObject")
public class DocumentConflictDto extends BaseDto {

    private Long documentId;

    @Schema(hidden = true)
    private Instant sendDate;

    @Schema(hidden = true)
    private Instant resolvingDate;

    @Schema(hidden = true)
    private String registrarName;

    @Schema(hidden = true)
    private Long senderId;

    @Schema(hidden = true)
    private String senderName;

    @Schema(hidden = true)
    private Long resolverId;

    @Schema(hidden = true)
    private String resolverName;

    private String registerDescription;

    private String resolveDescription;

    private List<ConflictReasonDto> conflictReasons;
}
