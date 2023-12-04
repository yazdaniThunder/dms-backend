package com.sima.dms.domain.dto;

import com.sima.dms.domain.enums.BIStatusEnum;
import com.sima.dms.domain.enums.ProcessStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class DocumentOcrDto implements Serializable {

    private String nodeDocumentUuid;
    private Set<String> dates;
    private Set<String> branchCodes;
    private Set<String> documentNumbers;
    private String documentType;
    private String ocrText;
    private String ocrNumber;
    private boolean typeMatches;
    private double levenshteinDistance;
    private double jaroWinklerDistance;

    private String realBranchCode;
    private List<String> realDates;

    private ProcessStateEnum processStateEnum;
    private String description;

    private BIStatusEnum biStatus;
    private String biDescription;

    private Long ocrProcessTime;
    private Long biProcessTime;

    public DocumentOcrDto(String nodeDocumentUuid) {
        this.nodeDocumentUuid = nodeDocumentUuid;
    }
}
