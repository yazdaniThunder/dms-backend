package com.sima.dms.tools.language;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;

public interface MessageConfigurator {

    LocaleResolver localeResolver();

    ResourceBundleMessageSource messageSource();

    ResourceBundleMessageSource persianMessage();

    ResourceBundleMessageSource exceptionMessageSource();

}
