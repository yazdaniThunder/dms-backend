package com.sima.dms.validation;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NationalCodeValidation {

    public final static String dateRegex = "^[1-4]\\d{3}\\/((0[1-6]\\/((3[0-1])|([1-2][0-9])|(0[1-9])))|((1[0-2]|(0[7-9]))\\/(30|([1-2][0-9])|(0[1-9]))))$";

    public static boolean validateNationalCode(String nationalCode) {
        nationalCode = persianToDecimal(nationalCode);

        String[] identicalDigits = {"0000000000", "1111111111", "2222222222", "3333333333", "4444444444", "5555555555", "6666666666", "7777777777", "8888888888", "9999999999"};

        if (nationalCode.trim().isEmpty()) {
            return false;
        } else if (nationalCode.length() != 10) {
            return false;
        } else if (Arrays.asList(identicalDigits).contains(nationalCode)) {
            return false;
        } else {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(nationalCode.charAt(i)) * (10 - i);
            }
            int lastDigit;
            int divideRemaining = sum % 11;

            if (divideRemaining < 2) {
                lastDigit = divideRemaining;
            } else {
                lastDigit = 11 - (divideRemaining);
            }

            return Character.getNumericValue(nationalCode.charAt(9)) == lastDigit;
        }
    }

    public static String persianToDecimal(String number) {
        if (number != null) {
            char[] chars = new char[number.length()];
            for (int i = 0; i < number.length(); i++) {
                char ch = number.charAt(i);
                if (ch >= 0x0660 && ch <= 0x0669)
                    ch -= 0x0660 - '0';
                else if (ch >= 0x06f0 && ch <= 0x06F9)
                    ch -= 0x06f0 - '0';
                chars[i] = ch;
            }
            return new String(chars);
        } else return "";
    }

    public static String removePersian(String String) {
        if (String != null) {
            char[] chars = new char[String.length()];
            for (int i = 0; i < String.length(); i++) {
                char ch = String.charAt(i);
                if (ch >= 0x0660 && ch <= 0x0669)
                    continue;
                else if (ch >= 0x06f0 && ch <= 0x06F9)
                    continue;
                chars[i] = ch;
            }
            return new String(chars);
        } else return "";
    }


    public static String checkDateExpression(String date) {
        Pattern datePattern = Pattern.compile(NationalCodeValidation.dateRegex);
        Matcher dateMatcher = datePattern.matcher(date);
        if (dateMatcher.find())
            return dateMatcher.group(0);
        return null;
    }
}
