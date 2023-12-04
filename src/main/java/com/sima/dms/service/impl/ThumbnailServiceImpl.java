package com.sima.dms.service.impl;

import com.sima.dms.repository.NodeDocumentRepository;
import com.sima.dms.service.FolderService;
import com.sima.dms.service.ThumbnailService;
import com.openkm.sdk4j.OKMWebservices;
import com.openkm.sdk4j.OKMWebservicesFactory;
import com.openkm.sdk4j.bean.Document;
import com.sima.dms.constants.OpenKM;
import com.sima.dms.tools.PdfToImage;
import com.sun.pdfview.PDFFile;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static com.sima.dms.tools.PdfToImage.pdf2Image;

@Service
//@Transactional
@AllArgsConstructor
public class ThumbnailServiceImpl implements ThumbnailService {

    private final FolderService folderService;
    private final NodeDocumentRepository nodeDocumentRepository;

    private final Logger log = LoggerFactory.getLogger(ThumbnailServiceImpl.class);

    private final OKMWebservices webservices = OKMWebservicesFactory.newInstance(OpenKM.host, OpenKM.username, OpenKM.password);

    @Override
    public Document createThumbnail(String documentUuid, MultipartFile content) {

        try {
            if (!folderService.isValidFolder("/okm:root/thumbnail"))
                folderService.createFolder("thumbnail");

            Document thumbnail = new Document();
            thumbnail.setPath("/okm:root/thumbnail/" + documentUuid + "_thumb" + ".png");
            thumbnail.setMimeType(MediaType.IMAGE_PNG_VALUE);

            if (content.getContentType().contains("image")) {
                thumbnail = webservices.createDocument(thumbnail, createThumbnailImage(content.getInputStream()));
            } else if (content.getContentType().contains("pdf")) {
                thumbnail = webservices.createDocument(thumbnail, createThumbnailImage(createThumbnailImage(content.getInputStream())));
            }
            nodeDocumentRepository.setThumbnail(documentUuid, thumbnail.getUuid());
            content.getInputStream().close();
            return thumbnail;

        } catch (Exception e) {
            log.debug("can not create thumbnail");
            return null;
        }
    }

    @Override
    public Document createThumbnail(String documentUuid, InputStream file, String mimeType) {

        try {

            if (!folderService.isValidFolder("/okm:root/thumbnail"))
                folderService.createFolder("thumbnail");

            Document thumbnail = new Document();
            thumbnail.setPath("/okm:root/thumbnail/" + documentUuid + "_thumb" + ".png");
            thumbnail.setMimeType(MediaType.IMAGE_PNG_VALUE);

            if (mimeType.contains("image")) {
                thumbnail = webservices.createDocument(thumbnail, createThumbnailImage(file));
            } else if (mimeType.contains("pdf")) {
                thumbnail = webservices.createDocument(thumbnail, createThumbnailImage(createThumbnailImage(file)));
            }
            nodeDocumentRepository.setThumbnail(documentUuid, thumbnail.getUuid());
            IOUtils.closeQuietly(file);
            return thumbnail;

        } catch (Exception e) {
            log.debug("can not create thumbnail");
            return null;
        }

    }

    private InputStream createThumbnailImage(InputStream is) throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedImage bufferedImage = ImageIO.read(is);
        Image image = bufferedImage.getScaledInstance(800, 500, Image.SCALE_DEFAULT);
        ImageIO.write((RenderedImage) image, "png", os);
        InputStream thumbInputStream = new ByteArrayInputStream(os.toByteArray());

        is.close();
        os.close();

        return thumbInputStream;
    }

    private InputStream firstPagePdfThumbnail(InputStream is) throws IOException {

        PDFFile pdffile = new PDFFile(ByteBuffer.wrap(IOUtils.toByteArray(is)));

        Image image = PdfToImage.pdf2Image(pdffile, 0);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) image, "png", os);
        InputStream fis = new ByteArrayInputStream(os.toByteArray());

        is.close();
        os.close();

        return fis;
    }

}
