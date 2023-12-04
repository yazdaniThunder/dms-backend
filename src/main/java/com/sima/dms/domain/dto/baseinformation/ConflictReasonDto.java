package com.sima.dms.domain.dto.baseinformation;

import com.sima.dms.domain.dto.common.BaseDto;
import com.sima.dms.domain.enums.ConflictTypeEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "ConflictReasonObject")
public class ConflictReasonDto extends BaseDto {

    private String reason;
    private ConflictTypeEnum type;
    private DocumentSetTypeEnum documentSetType;
}
