package com.sima.dms.domain.dto.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "SetMetadataRequestObject")
public class SetMetadataRequest {

   private String documentUuid;
   private String groupName;
   private String fieldName;
   private String value;
}
