package com.sima.dms.constants;

import com.sima.dms.tools.JalaliCalendar;

import java.time.Instant;
import java.util.Date;

/**
 * Application constants.
 */
public final class OpenKM {

    public static final String host = "http://localhost:8080/OpenKM";
//        public static final String host = "http://localhost:4200/OpenKM";
    public static final String username = "okmAdmin";
    public static final String password = "admin";

    public static final String rootPath = "/okm:root/";
    public static final String documentRequestFolderPath = "requests/";
    public static final String documentRequestFolder = "requests";
    public static final String documentSetPath = "documentSets/";
    public static final String documentSetFolder = "documentSets";
    public static final String provincePath = "province/";
    public static final String provinceFolder = "province";

    public static final String otherDocumentFolderPath = "otherDocument/";
    public static final String otherDocumentFolder = "otherDocument";
    public static final String otherDocumentFolderTemp = "otherDocumentTemp/";

    private static JalaliCalendar.YearMonthDate yearMonthDate = JalaliCalendar.getJalaliFullDate(Date.from(Instant.now()));

    public static final String yearPatch = yearMonthDate.getYear() + "/";
    public static final String monthPatch = (yearMonthDate.getMonth() + 1) + "/";
    public static final String dayPatch = yearMonthDate.getDate() + "/";

    public static final String yearFolder = String.valueOf(yearMonthDate.getYear());
    public static final String monthFolder = String.valueOf(yearMonthDate.getMonth() + 1);
    public static final String dayFolder = String.valueOf(yearMonthDate.getDate());
}
