package com.sima.dms.tools;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class PdfToImage {

    public static BufferedImage pdf2Image(InputStream is) throws IOException {

        PDFFile pdffile = null;
        pdffile = new PDFFile(ByteBuffer.wrap(IOUtils.toByteArray(is)));
        PDFPage page = pdffile.getPage(0);

        Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());

        Image image = page.getImage(rect.width, rect.height, //width & height
                rect, // clip rect
                null, // null for the ImageObserver
                true, // fill background with white
                true  // block until drawing is done
        );
        return (BufferedImage) image;
    }


    public static Image pdf2Image(PDFFile pdffile, int i) {

        PDFPage pdfPage = pdffile.getPage(i);
        Rectangle rect = new Rectangle(0, 0, (int) pdfPage.getBBox().getWidth(), (int) pdfPage.getBBox().getHeight());
        return pdfPage.getImage(rect.width, rect.height, rect, null, true, true);
    }

    public static Image pdf2Image(PDFPage pdfPage) {

        Rectangle rect = new Rectangle(0, 0, (int) pdfPage.getBBox().getWidth(), (int) pdfPage.getBBox().getHeight());
        return pdfPage.getImage(rect.width, rect.height, rect, null, true, true);
    }

    public static Set<Image> pdf2Image(PDFFile pdfFile) {

        Set<Image> images = new HashSet<>();

        if (pdfFile != null) {

            for (int i = 0; i < pdfFile.getNumPages(); i++) {
                PDFPage page = pdfFile.getPage(i);
                Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());
                images.add(page.getImage(rect.width, rect.height, rect, null, true, true));
            }
        }
        return images;
    }
}
