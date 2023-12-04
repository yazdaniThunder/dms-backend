package com.sima.dms.domain.dto;

import lombok.Data;

@Data
public class TransactionDto {

    private Long id;

    private String transactionCode;

    private String documentClass;

    private String subsystemName;

    private String description;
}
