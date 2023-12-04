package com.sima.dms.domain.dto.request;


import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "FixConflictDocumentRequestObject")
public class FixConflictDocumentRequestDto {

   private Long documentId;
   private String description;

}
