package com.sima.dms.domain.enums;

public enum BranchTypeEnum {

    BRANCH("0","شعبه") ,
    COUNTER("1","باجه"),
    CORPORATE_BANKING("2","بانکداری شرکتی");

    private String code;
    private String persianTitle;

    BranchTypeEnum(String code , String persianTitle){
        this.code = code;
        this.persianTitle = persianTitle;
    }
    private String getCode(){
        return code;
    }
    private String getPersianTitle(){
        return persianTitle;
    }
}
