package com.sima.dms.tools;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class TifToPdf {

    public static ByteArrayOutputStream tifToPdf(InputStream inputStream) throws IOException {
        PDDocument document = new PDDocument();

        ImageInputStream isb = ImageIO.createImageInputStream(inputStream);

        Iterator<ImageReader> iterator = ImageIO.getImageReaders(isb);
        if (iterator == null || !iterator.hasNext()) {
            throw new IOException("Image file format not supported by ImageIO: ");
        }

        ImageReader reader = (ImageReader) iterator.next();
        iterator = null;
        reader.setInput(isb);

        int nbPages = reader.getNumImages(true);

        for (int p = 0; p < nbPages; p++) {
            BufferedImage bufferedImage = reader.read(p);

            PDPage page = new PDPage();
            document.addPage(page);

            PDImageXObject i = LosslessFactory.createFromImage(document, bufferedImage);

            PDPageContentStream content = new PDPageContentStream(document, page);
            content.drawImage(i, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());

            content.close();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();
        return baos;
    }

}