package com.sima.dms.domain.enums;

import org.springframework.security.core.GrantedAuthority;

public enum RoleEnum implements GrantedAuthority {
    USER("کاربر"),
    ADMIN("ادمین"),
    BU("کاربر شعبه"),
    BA("رئیس شعبه"),
    DOPU("کاربر اداره اسناد-گروه فیزیکی"),
    DOEU("کاربر اداره اسناد-گروه الکترونیکی"),
    DOA("رئیس اداره اسناد"),
    RU("کاربر گزارشات");
    private final String persianTitle;
    RoleEnum(String persianTitle) {
        this.persianTitle = persianTitle;
    }
    public static String getPersianTitle(RoleEnum roleEnum){
        return roleEnum.persianTitle;
    }
    @Override
    public String getAuthority() {
        return this.name();
    }
}
