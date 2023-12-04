package com.sima.dms.domain.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Calendar;
import java.util.Set;

@Data
@ApiModel(value = " NodeBaseObject")
public class NodeBaseDto {

    protected String uuid;
    protected String name;
    protected Calendar created;
    protected Set<String> categories;
    protected Set<NodePropertyDto> properties;

//    protected String parent;
//    protected String context;
//    protected String path;
//    protected String author;

}
