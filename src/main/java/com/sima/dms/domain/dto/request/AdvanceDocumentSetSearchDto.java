package com.sima.dms.domain.dto.request;

import com.sima.dms.domain.enums.DocumentSetStateEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class AdvanceDocumentSetSearchDto {

    private DocumentSetTypeEnum type;
    private List<DocumentSetStateEnum> status;
    private String fromDate;
    private String toDate;
    private String registerFromDate;
    private String registerToDate;
    private String sentFromDate;
    private String sentToDate;
    private Long registrarId;
    private Long confirmerId;
    private Long scannerId;
    private String rowNumber;
    private List<Long> branchIds;
    private String reason;
    private String fileNumber;
    private String customerNumber;
    private Long fileStatusId;
    private Long fileTypeId;
}
