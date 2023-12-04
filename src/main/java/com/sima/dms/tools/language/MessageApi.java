package com.sima.dms.tools.language;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageApi {

    @Autowired
    MessageConfigurator messageConfigurator;

    public String getMessageByCode(String errorCode, Locale locale) {
        return messageConfigurator.messageSource().getMessage(errorCode, null, locale);
    }


    public String getErrorCodeByExceptionName(String exceptionClassName) {
        Locale locale = new Locale("en");
        return messageConfigurator.exceptionMessageSource().getMessage(exceptionClassName, null, locale);
    }


    public String getPersianMessageByCode(String errorCode, Locale locale) {
        return messageConfigurator.persianMessage().getMessage(errorCode, null, locale);
    }
}
