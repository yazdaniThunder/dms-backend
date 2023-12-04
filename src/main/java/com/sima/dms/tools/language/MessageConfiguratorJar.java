package com.sima.dms.tools.language;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Controller
public class MessageConfiguratorJar implements MessageConfigurator {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        return slr;
    }

    @Bean
    @Primary
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename("messages");
        rs.setUseCodeAsDefaultMessage(true);
        return rs;

    }

    @Bean
    public ResourceBundleMessageSource persianMessage() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename("persian_error_desc");
        rs.setDefaultEncoding("UTF8");
        rs.setUseCodeAsDefaultMessage(true);
        return rs;

    }


    @Bean
    public ResourceBundleMessageSource exceptionMessageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename("exception");
        rs.setUseCodeAsDefaultMessage(true);
        return rs;

    }


}
