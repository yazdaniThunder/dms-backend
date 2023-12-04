package com.sima.dms.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Data
@Component
public class PropertiesConfig {

    @Value("${server.port}")
    public static int serverPort;

    @Value("${spring.datasource.url}")
    public static String dataSourceUrl;

    @Value("${spring.datasource.driver-class-name}")
    public static String dataSourceDriverClassName;

    @Value("${spring.datasource.username}")
    public static String dataSourceUsername;

    @Value("${spring.datasource.password}")
    public static String dataSourcePassword;

    @Value( "${cleanup.bat.directory}" )
    private static String cleanupBatDirectory;
}
