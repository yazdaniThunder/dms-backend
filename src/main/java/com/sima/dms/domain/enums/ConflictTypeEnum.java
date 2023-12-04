package com.sima.dms.domain.enums;

public enum ConflictTypeEnum {

    DOCUMENT("01", "فایل"),
    DOCUMENT_SET("02", "دسته سند");

    private String code;
    private String persianName;

    ConflictTypeEnum(String code, String persianName) {
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
