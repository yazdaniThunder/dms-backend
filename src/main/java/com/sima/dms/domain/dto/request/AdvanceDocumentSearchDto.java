package com.sima.dms.domain.dto.request;

import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import com.sima.dms.domain.enums.DocumentStateEnum;
import lombok.Data;

import java.util.List;

@Data
public class AdvanceDocumentSearchDto {

    private String maintenanceCode;
    private List<DocumentStateEnum> states;
    private DocumentSetTypeEnum type;
    private String fromDate;
    private String toDate;
    private String registerFromDate;
    private String registerToDate;
    private String sentFromDate;
    private String sentToDate;
    private Long registrarId;
    private Long confirmerId;
    private Long scannerId;
    private String filename;
    private List<Long> branchIds;
    private String documentNumber;
    private String documentDate;
    private String conflictRegisterDate;
    private String reason;
    private String rowNumber;
    private String fileNumber;
    private String customerNumber;
    private Long fileStatusId;
    private Long fileTypeId;
}
