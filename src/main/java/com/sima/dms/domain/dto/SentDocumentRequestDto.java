package com.sima.dms.domain.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class SentDocumentRequestDto {
    @NotNull
    private Long id;

    private Instant expiryDate;

    private String receiveDescription;
}
