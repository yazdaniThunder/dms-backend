package com.sima.dms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
public class RoleDto implements Serializable {

    private Long id;

    private String name;

    private String initials;

    private String description;

    @JsonIgnore
    public String getAuthority() {
        return this.getInitials();
    }
}