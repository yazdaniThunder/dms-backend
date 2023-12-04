package com.sima.dms.domain.dto;

import com.sima.dms.domain.enums.BIStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
public class OcrDocumentReportDto {
    private Long id;
    private Long fileSize;
    private Instant uploadStart;
    private Instant uploadEnd;
    private Instant ocrFinishedTime;
    private Instant registerDate;
    private Long countDoc;
    private String fileFormat;
    private BIStatusEnum biStatus;
    private Long countAllDoc;
    private Long branchCode;
    private String branchName;
}
