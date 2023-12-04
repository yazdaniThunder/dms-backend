package com.sima.dms.tools;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class ImageEnhancement {

    public static BufferedImage setFilter(BufferedImage bufferedImage) {

        float offset = 0;
        float scaleFactor = 0;

        double d = bufferedImage.getRGB(bufferedImage.getTileWidth() / 2, bufferedImage.getTileHeight() / 2);

        if (d >= -1.4211511E7 && d < -7254228) {
            scaleFactor = 3f;
            offset = -10f;
        } else if (d >= -7254228 && d < -2171170) {
            scaleFactor = 1.455f;
            offset = -47f;
        } else if (d >= -2171170 && d < -1907998) {
            scaleFactor = 1.35f;
            offset = -10f;
        } else if (d >= -1907998 && d < -257) {
            scaleFactor = 1.19f;
            offset = 0.5f;
        } else if (d >= -257 && d < -1) {
            scaleFactor = 1f;
            offset = 0.5f;
        } else if (d >= -1 && d < 2) {
            scaleFactor = 1f;
            offset = 0.35f;
        }
        RescaleOp rescale = new RescaleOp(scaleFactor, offset, null);
        return rescale.filter(bufferedImage, null);
    }

    public static BufferedImage grayScale(BufferedImage bufferedImage) {

        BufferedImage newImage = bufferedImage;

        int width = newImage.getWidth();
        int height = newImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int p = newImage.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int avg = (r + g + b) / 3;

                p = (a << 24) | (avg << 16) | (avg << 8) | avg;
                newImage.setRGB(x, y, p);
            }
        }
        return newImage;
    }

    public static BufferedImage enhancementContrast(BufferedImage bufferedImage, float brightenFactor) {
        RescaleOp op = new RescaleOp(brightenFactor, 0, null);
        return op.filter(bufferedImage, null);
    }

}
