package com.sima.dms.domain.dto.baseinformation;

import com.sima.dms.domain.dto.common.BaseDto;
import com.sima.dms.domain.entity.baseinformation.OtherDocumentType;
import com.sima.dms.domain.entity.baseinformation.FileStatus;
import com.sima.dms.domain.enums.FileTypeEnum;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(value = "FileTypeObject")
public class FileTypeDto extends BaseDto {

    @NotNull
    private String title;

    private Boolean activateFileNumber = true;

    private List<FileStatusDto> fileStatuses;

    private List<OtherDocumentTypeDto> otherDocumentTypes;
}
