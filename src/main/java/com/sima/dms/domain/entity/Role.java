package com.sima.dms.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sima.dms.domain.dto.RoleDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "DMS_ROLE")
public class Role implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = true, unique = true)
    private String name;

    @Column(name = "initials", nullable = true, unique = true)
    private String initials;

    @Column(nullable = true, unique = true)
    private String description;

    public Role(String name) {
        this.name = name;
        this.initials = name;
        this.description = name;
    }

    public Role(RoleDto roleDto) {
        this.name = roleDto.getName();
        this.initials = roleDto.getInitials();
        this.description = roleDto.getDescription();
    }

    @JsonIgnore
    @Override
    public String getAuthority() {
        return this.getInitials();
    }

}