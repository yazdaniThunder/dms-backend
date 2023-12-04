package com.sima.dms.domain.dto;

import com.sima.dms.domain.enums.MetadataFieldNameEnum;
import com.sima.dms.domain.enums.TransactionType;
import lombok.Data;

import java.util.List;

@Data
public class MetadataTreeDto {

    private Long id;
    private String name;
    private Long parentId;
    private MetadataFieldNameEnum fieldName;
    private List<MetadataTreeDto> children;
    private TransactionType transactionType;
}
