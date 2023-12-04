package com.sima.dms.domain.enums;

public enum RequestTypeEnum {

    DOCUMENT_IMAGE("01" , "تصویر سند"),
    DOCUMENT_ORIGINAL("02" , "اصل سند"),
    GROUP_DOCUMENT_IMAGE("03" , "تصویر سند گروهی");

    private String code;
    private String persianTitle;

    RequestTypeEnum(String code , String persianTitle){
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
