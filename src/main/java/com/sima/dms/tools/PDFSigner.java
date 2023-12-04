/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.sima.dms.tools;

import com.lowagie.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;


/**
 * Text extractor for image documents.
 * Use OCR from http://code.google.com/p/tesseract-ocr/
 */

@Slf4j
public class PDFSigner {

    private static final int ESTIMATED_SIGNATURE_SIZE = 8192;

    private static byte[] certificateChain;
    private static Certificate[] certificates;
    private static PrivateKey privateKey;

    public PDFSigner() {
        try {
//          InputStream inputStream = new FileInputStream("D:\\projects\\dms\\src\\main\\resources\\keypair\\dms.p12");
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("dms.p12");
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            final char[] password = "simadms".toCharArray();
            keyStore.load(inputStream, password);

            X509Certificate certificate = (X509Certificate) keyStore.getCertificate("DMS");

            this.privateKey = (PrivateKey) keyStore.getKey("DMS", "simadms".toCharArray());
            this.certificateChain = certificate.getEncoded();
            this.certificates = new Certificate[]{certificate};

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void sign(byte[] document, ByteArrayOutputStream output) {
        try {
            PdfReader pdfReader = new PdfReader(document);

            PdfStamper signer = PdfStamper.createSignature(pdfReader, output, '\0');

            Calendar signDate = Calendar.getInstance();

            int page = 1;

            PdfSignature pdfSignature = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
            pdfSignature.setName("DMS");
            pdfSignature.setReason("DMS");
            pdfSignature.setLocation("Tehran");
            pdfSignature.setContact("dms@sb24.non");
            pdfSignature.setDate(new PdfDate(signDate));
            pdfSignature.setCert(certificateChain);

            PdfSignatureAppearance appearance = createAppearance(signer, page, pdfSignature);

            PdfPKCS7 sgn = new PdfPKCS7(null, certificates, null, "SHA-256", null, false);
            InputStream data = appearance.getRangeStream();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(IOUtils.toByteArray(data));
            byte[] appeareanceHash = digest.digest();

            byte[] hashToSign = sgn.getAuthenticatedAttributeBytes(appeareanceHash, appearance.getSignDate(), null);

            byte[] signedHash = addDigitalSignatureToHash(hashToSign);

            sgn.setExternalDigest(signedHash, null, "RSA");
            byte[] encodedPKCS7 = sgn.getEncodedPKCS7(appeareanceHash, appearance.getSignDate());

            byte[] paddedSig = new byte[ESTIMATED_SIGNATURE_SIZE];

            System.arraycopy(encodedPKCS7, 0, paddedSig, 0, encodedPKCS7.length);

            PdfDictionary dictionary = new PdfDictionary();
            dictionary.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
            appearance.close(dictionary);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private static PdfSignatureAppearance createAppearance(PdfStamper signer, int page, PdfSignature pdfSignature) {
        try {
            PdfSignatureAppearance appearance = signer.getSignatureAppearance();
            appearance.setRender(PdfSignatureAppearance.SignatureRenderDescription);
            appearance.setAcro6Layers(true);

//            int lowerLeftX = 570;
//            int lowerLeftY = 70;
//            int width = 370;
//            int height = 150;
//            appearance.setVisibleSignature(new Rectangle(lowerLeftX, lowerLeftY, width, height), page, null);

            appearance.setCryptoDictionary(pdfSignature);
            appearance.setCrypto(null, certificates, null, PdfName.FILTER);

            HashMap<Object, Object> exclusions = new HashMap<>();
            exclusions.put(PdfName.CONTENTS, ESTIMATED_SIGNATURE_SIZE * 2 + 2);
            appearance.preClose(exclusions);
            return appearance;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] addDigitalSignatureToHash(byte[] hashToSign) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(hashToSign);
            return signature.sign();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static InputStream signPDF(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        sign(IOUtils.toByteArray(inputStream), byteArrayOutputStream);
        ByteArrayInputStream signedDocument = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return signedDocument;

//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        try (InputStream fileInputStream = new FileInputStream(docFile)) {
//            sign(IOUtils.toByteArray(fileInputStream), output);
//        }
//        log.info("\n..........Sinning the document has finished and is being saved into database");
//
//        File result = new File(docFile + ".pdf");
//        FileUtils.writeByteArrayToFile(result, output.toByteArray());
//        output.close();
//        log.info("\nThe signed document has finished");
    }
}
