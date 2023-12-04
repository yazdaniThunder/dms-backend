package com.sima.dms.domain.enums;

public enum DocumentSetTypeEnum {

    DAILY("01", "روزانه","روزانه"),
    CHAKAVAK("02", "چکاوک","چکاوک"),
    OTHER_BANKING_OPERATIONS_DEPOSIT("03", "سایر اسناد عملیات بانکی-سپرده", "سپرده"),
    OTHER_BANKING_OPERATIONS_CREDIT("04",  "سایر اسناد عملیات بانکی-اعتباری","اعتباری"),
    OTHER_BANKING_OPERATIONS_CURRENCY("05",  "سایر اسناد عملیات بانکی-ارزی","ارزی"),
    OTHER_BANKING_OPERATIONS_ELECTRONIC_BANKING("06",  "سایر اسناد عملیات بانکی-بانکداری الکترونیک","بانکداری الکترونیک"),
    OTHER_BANKING_OPERATIONS_SAFE_BOX("07", "سایر اسناد عملیات بانکی-صندوق امانات","صندوق امانات");

    private String code;
    private String persianName;
    private String title;

    DocumentSetTypeEnum(String code, String persianName, String title) {
        this.code = code;
        this.persianName = persianName;
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public String getPersianName() {
        return persianName;
    }

    public String getTitle() {
        return title;
    }
}
