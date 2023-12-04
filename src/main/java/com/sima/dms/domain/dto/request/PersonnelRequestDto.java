package com.sima.dms.domain.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonnelRequestDto {
    private String cfcifNo;
    private String accountName;
    private String personelCode;
    public PersonnelRequestDto(String accountName) {
        this.cfcifNo="";
        this.personelCode="";
        this.accountName = accountName;
    }
}
