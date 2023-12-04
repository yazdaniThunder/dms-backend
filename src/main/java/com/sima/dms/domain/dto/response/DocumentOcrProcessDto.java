package com.sima.dms.domain.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
public class DocumentOcrProcessDto {
    private Long branchCode;
    private Instant fromDate;
    private Instant toDate;
}
