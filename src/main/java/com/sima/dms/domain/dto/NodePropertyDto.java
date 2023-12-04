package com.sima.dms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "NodePropertyObject")
public class NodePropertyDto{

    @JsonIgnore
    private Long id;
    @JsonIgnore
    private String nodeId;
    @JsonIgnore
    private String group;

    private String name;
    private String value;
    private PropertyValidationDto validation;

}
