package com.sima.dms.domain.enums;

public enum DocumentRequestTypeEnum {
    DAILY("01", "روزانه"),
    CHAKAVAK("02","چکاوک" ),
    OTHER_BANKING_OPERATIONS("03", "سایر اسناد عملیات بانکی");
    private String code;
    private String persianName;

    DocumentRequestTypeEnum(String code, String persianName) {
        this.code = code;
        this.persianName = persianName;
    }

    public String getCode() {
        return code;
    }

    public String getPersianName() {
        return persianName;
    }

}
