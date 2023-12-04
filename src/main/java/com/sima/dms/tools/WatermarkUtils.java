//package com.negah.dms.tools;
//
//import com.itextpdf.text.Font;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.Rectangle;
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.*;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.geom.AffineTransform;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//
//public class WatermarkUtils {
//
//    public static ByteArrayOutputStream addWatermark(InputStream is, String text) {
//        PdfReader reader = null;
//        PdfStamper stamper = null;
//        try (
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ) {
//            // read existing pdf
//            reader = new PdfReader(is);
//            stamper = new PdfStamper(reader, baos);
//
//            // text watermark
//            Font font = new Font(Font.FontFamily.HELVETICA, 34, Font.BOLD, new GrayColor(0.5f));
//            Phrase p = new Phrase(text, font);
//
//            // image watermark
//            URL url= WatermarkUtils.class.getResource("sima.png");
//            Image img = Image.getInstance(url) ;
//            float w = img.getScaledWidth();
//            float h = img.getScaledHeight();
//            // properties
//            PdfContentByte over;
//            Rectangle pagesize;
//            float x, y;
//            // loop over every page
//            int n = reader.getNumberOfPages();
//            for (int i = 1; i <= n; i++) {
//                // get page size and position
//                pagesize = reader.getPageSizeWithRotation(i);
//                x = (pagesize.getLeft() + pagesize.getRight()) / 2;
//                y = (pagesize.getTop() + pagesize.getBottom()) / 2;
//                over = stamper.getOverContent(i);
//                over.saveState();
//                // set transparency
//                PdfGState state = new PdfGState();
//                state.setFillOpacity(0.2f);
//                over.setGState(state);
//                // add watermark text and image
////                if (i % 2 == 1) {
//                for (int c = 0; c < 10; c++) {
//                    ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, c * 200, 45f);
//                }
//                over.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2));
////                }
////                else {
////                    over.addImage(img, w, 0, 0, h, x - (w / 2), y - (h / 2));
////                }
//                over.restoreState();
//            }
//            stamper.close();
//            reader.close();
//            return baos;
//        } catch (IOException | DocumentException e) {
//            try {
//                stamper.close();
//            } catch (DocumentException | IOException documentException) {
//                documentException.printStackTrace();
//            }
//            reader.close();
//            return null;
//        } finally {
//            try {
//                stamper.close();
//            } catch (DocumentException | IOException e) {
//                e.printStackTrace();
//            }
//            reader.close();
//        }
//    }
//
//    public static ByteArrayOutputStream addTextWatermarkOnImage(InputStream is, String text) throws IOException {
//        try (
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ) {
//            BufferedImage sourceImage = ImageIO.read(is);
//            Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
//            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
//            g2d.setComposite(alphaChannel);
//            g2d.setColor(Color.GRAY);
//            int fontSize = (int) sourceImage.getHeight() / 20;
//            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, fontSize));
//            AffineTransform trans = new AffineTransform();
//            trans.rotate(Math.toRadians(45f));
//            g2d.transform(trans);
//            for (int c = -10; c < 11; c++) {
//                g2d.drawString(text, sourceImage.getWidth() / 3, ((sourceImage.getWidth() / 2 / 5) * c));
//            }
//            ImageIO.write(sourceImage, "png", baos);
//            g2d.dispose();
////            System.out.println("The tex watermark is added to the image.");
//            return baos;
//        }
//    }
//
//}
