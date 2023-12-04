package com.sima.dms.tools;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipFile {

    public static Map<String, InputStream> unzip(InputStream in) {

        Tika tika = new Tika();
        Map<String, InputStream> files = new HashMap<>();

        byte[] buffer = new byte[1024];

        try {
            ZipInputStream zis = new ZipInputStream(in);
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {

                if (!zipEntry.isDirectory()) {

                    ByteArrayOutputStream fos = new ByteArrayOutputStream();
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fos.toByteArray());
                    if ((tika.detect(byteArrayInputStream).equals("image/tif")) ||
                            (tika.detect(byteArrayInputStream).equals("image/tiff")) ||
                            (tika.detect(byteArrayInputStream).equals("application/pdf") && FilenameUtils.getExtension(zipEntry.getName()).equals("pdf")))
                        files.put(zipEntry.getName(), byteArrayInputStream);


                    fos.close();
                    zis.closeEntry();
                    zipEntry = zis.getNextEntry();
                }
            }
            zis.closeEntry();
            zis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

//    public static Map<String, InputStream> unzip(InputStream in) throws IOException {
//
//        Map<String, InputStream> result = new HashMap<>();
//        Tika tika = new Tika();
//        File tmpDir = new File(getTempDir());
//        final byte[] buffer = new byte[1024];
//        ZipInputStream zis = null;
//        try {
//            zis = new ZipInputStream(in);
//            ZipEntry zipEntry = zis.getNextEntry();
//            while (zipEntry != null) {
//
//                File newFile = newFile(tmpDir, zipEntry);
//
//                if (!zipEntry.isDirectory()) {
//                    try {
//                        File parent = newFile.getParentFile();
//
//                        if (!parent.isDirectory() && !parent.mkdirs())
//                            throw new IOException("Failed to create directory " + parent);
//
//                        int len;
//                        final FileOutputStream fos = new FileOutputStream(newFile);
//                        while ((len = zis.read(buffer)) > 0) {
//                            fos.write(buffer, 0, len);
//                        }
//                        fos.close();
//                        if (tika.detect(newFile).equals("application/pdf") && FilenameUtils.getExtension(newFile.getName()).equals("pdf"))
//                            result.put(zipEntry.getName(), new FileInputStream(newFile));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        deleteQuietly(newFile);
//                    }
//                }
//                zipEntry = zis.getNextEntry();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            zis.closeEntry();
//            zis.close();
//            deleteQuietly(tmpDir);
//        }
//        return result;
//    }
//
//    private static String getTempDir() throws IOException {
//        String tempDir = System.getProperty("java.io.tmpdir");
//
//        if (tempDir == null) {
//            throw new IOException("Cannot find temp dir");
//        }
//        File destDir = new File(tempDir + "/" + UUID.randomUUID());
//        destDir.mkdir();
//        return destDir.getPath();
//    }
//
//    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
//        File destFile = new File(destinationDir, zipEntry.getName());
//
//        String destDirPath = destinationDir.getCanonicalPath();
//        String destFilePath = destFile.getCanonicalPath();
//
//        if (!destFilePath.startsWith(destDirPath + File.separator)) {
//            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
//        }
//
//        return destFile;
//    }
//
//    public static void deleteQuietly(File file) {
//        org.apache.commons.io.FileUtils.deleteQuietly(file);
//    }
}