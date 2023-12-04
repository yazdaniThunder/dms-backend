package com.sima.dms.domain.dto.request;


import com.sima.dms.domain.enums.DocumentRequestStateEnum;
import com.sima.dms.domain.enums.DocumentRequestTypeEnum;
import com.sima.dms.domain.enums.DocumentSetTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class RequestSearchParameterDto {

    private String registerDateFrom;

    private String registerDateTo;

    private String documentNumber;

    private String documentDateFrom;

    private String documentDateTo;

    private String customerNumber;

    private DocumentRequestTypeEnum documentType;

    private DocumentRequestStateEnum state;

    private Long creatorId;

    private Long confirmerId;

    private List<Long> documentBranchIds;

    private List<Long> requestBranchIds;

    private String sentDateFrom;

    private String sentDateTo;
}
