package com.sima.dms.tools;

import org.apache.commons.io.FilenameUtils;

import java.time.Instant;

public class FileUtils {

    public static String getFormat(String filename) {
        return '.' + FilenameUtils.getExtension(filename);
    }

    public static String uniqueName(String name) {
        String date = String.valueOf(Instant.now());
        date = date.replace(".", "");
        date = date.replace(":", "");
        date = date.replace("-", "");

        if (name == null || name.isEmpty())
            name = date;
        else name = name + '_' + date;

        return name;
    }

    public static String getInstant() {
        String instant = String.valueOf(Instant.now());
        instant = instant.replace(".", "");
        instant = instant.replace(":", "");
        instant = instant.replace("-", "");
        return instant;
    }
}