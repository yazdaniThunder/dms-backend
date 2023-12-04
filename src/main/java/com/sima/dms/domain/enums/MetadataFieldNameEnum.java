package com.sima.dms.domain.enums;

public enum MetadataFieldNameEnum {

    /* daily documents metadata*/
    date("okg:Accounting", "okp:Accounting.date"),
    documentNo("okg:Accounting", "okp:Accounting.docNo"),
    branchCode("okg:Accounting", "okp:Accounting.branchCode"),
    documentClass("okg:Accounting", "okp:Accounting.documentClass"),
    documentSetType("okg:Accounting", "okp:Accounting.documentSetType"),
    documentDescription("okg:Accounting", "okp:Accounting.documentDescription"),
    levenshteinDistance("okg:Accounting", "okp:Accounting.levenshteinDistance"),
    jaroWinklerDistance("okg:Accounting", "okp:Accounting.jaroWinklerDistance"),
    dailyCompleted("okg:Accounting", "okp:Accounting.completed"),


    /* chakavak documents metadata*/
    sayyadNo("okg:Chakavak", "okp:Chakavak.sayyadNo"),
    chequeNo("okg:Chakavak", "okp:Chakavak.chequeNo"),
    chequeDate("okg:Chakavak", "okp:Chakavak.chequeDate"),
    fromBranch("okg:Chakavak", "okp:Chakavak.fromBranch"),
    toBranch("okg:Chakavak", "okp:Chakavak.toBranch"),
    chequeClass("okg:Chakavak", "okp:Chakavak.documentClass"),
    documentType("okg:Chakavak", "okp:Chakavak.documentType"),
    chakavakCompleted("okg:Chakavak", "okp:Chakavak.completed");

    private String group;
    private String value;

    MetadataFieldNameEnum(String group, String value) {
        this.group = group;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getGroup() {
        return group;
    }

    public static MetadataFieldNameEnum getFieldName(String value) {

        switch (value) {
            case "okp:Accounting.docNo":
                return documentNo;
            case "okp:Accounting.branchCode":
                return branchCode;
            case "okp:Accounting.date":
                return date;
            case "okp:Accounting.documentSetType":
                return documentSetType;
            case "okp:Accounting.documentClass":
                return documentClass;
            case "okp:Accounting.documentDescription":
                return documentDescription;
            case "okp:Accounting.levenshteinDistance":
                return levenshteinDistance;
            case "okp:Accounting.jaroWinklerDistance":
                return jaroWinklerDistance;
            case "okp:Accounting.completed":
                return dailyCompleted;

            case "okp:Chakavak.sayyadNo":
                return sayyadNo;
            case "okp:Chakavak.chequeNo":
                return chequeNo;
            case "okp:Chakavak.chequeDate":
                return chequeDate;
            case "okp:Chakavak.fromBranch":
                return fromBranch;
            case "okp:Chakavak.toBranch":
                return toBranch;
            case "okp:Chakavak.documentClass":
                return chequeClass;
            case "okp:Chakavak.documentType":
                return documentType;
            case "okp:Chakavak.completed":
                return chakavakCompleted;
        }
        return null;
    }
}
