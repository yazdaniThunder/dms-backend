package com.sima.dms.domain.enums;

public enum TransactionType {

    K_81_3("خرید کالا"),
    K_80_1("پرداخت وجه"),
    K_80_3("واریز قبض"),
    K_89_7("اعلام موجودی"),
    L_14_2("انتقالی"),
    K_81_1("انتقال وجه داخلی");

    private String persianName;

    TransactionType(String persianName) {
        this.persianName = persianName;
    }

}
