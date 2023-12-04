package com.sima.dms.domain.dto.documentSet;

import com.sima.dms.domain.dto.baseinformation.ConflictReasonDto;
import com.sima.dms.domain.dto.common.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@ApiModel(value = " DocumentSetConflictObject")
public class DocumentSetConflictDto extends BaseDto {

    @Schema(hidden = true)
    private Instant resolvingDate;

    @Schema(hidden = true)
    private String registrarName;

    @Schema(hidden = true)
    private Long resolverId;

    @Schema(hidden = true)
    private String resolverName;

    private String registerDescription;

    private String resolveDescription;

    private Long documentSetId;

    private List<ConflictReasonDto> conflictReasons;
}
