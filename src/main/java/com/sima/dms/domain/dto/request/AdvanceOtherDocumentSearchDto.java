package com.sima.dms.domain.dto.request;

import com.sima.dms.domain.enums.OtherDocumentStateEnum;
import lombok.Data;

import java.util.List;

@Data
public class AdvanceOtherDocumentSearchDto {

    private String registerFromDate;
    private String registerToDate;
    private String customerNumber;
    private String fileNumber;
    private Long fileTypeId;
    private Long otherDocumentTypeId;
    private Long fileStatusId;
    private Long registrarId;
    private List<Long> branchIds;
    private OtherDocumentStateEnum state;
}
