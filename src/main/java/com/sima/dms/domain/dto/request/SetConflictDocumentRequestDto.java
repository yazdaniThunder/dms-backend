package com.sima.dms.domain.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "SendConflictDocumentRequestObject")
public class SetConflictDocumentRequestDto {

   private Long documentId;
   private String description;
   private List<Long> conflictReasons;
}
