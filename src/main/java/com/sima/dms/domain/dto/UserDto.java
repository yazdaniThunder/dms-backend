package com.sima.dms.domain.dto;


import com.sima.dms.domain.dto.response.PersonnelResponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@ApiModel(value = "UserObject")
public class UserDto {

    private Long id;

    private Long cfCiNo;
    private Long personCode;
    private String nationalKey;
    private String personelUserName;

    private String firstName;
    private String lastName;
    private String fullName;
    private String completeName;
    private String fatherName;
    private boolean isIntActiveCode;
    private String isIntActiveDesc;
    @Schema(hidden = true)
    private boolean active;

    public UserDto(PersonnelResponseDto dto) {
        this.cfCiNo = dto.getCfCifNo();
        this.personCode = dto.getPrsnCode();
        this.nationalKey = dto.getNationalKey();
        this.personelUserName = dto.getPersonelUserName().toLowerCase();

        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.fullName = dto.getFullName();
        this.completeName = dto.getCompleteName();
        this.fatherName=dto.getFatherName();

        this.isIntActiveCode = dto.isIntActiveCode();
        this.isIntActiveDesc = dto.getIsIntActiveDesc();
        this.active = true;
    }
}
