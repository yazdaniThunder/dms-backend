package com.sima.dms.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentReceiveResponseDto {
    //private String fileUuid;
    private Instant expiryDate;
    private String receiveDescription;
   // private String name;
}
