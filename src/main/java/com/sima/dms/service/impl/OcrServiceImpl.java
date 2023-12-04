//package com.sima.dms.service.impl;
//
//import com.sima.dms.domain.dto.DocumentOcrDto;
//import com.sima.dms.domain.dto.request.MetadataDto;
//import com.sima.dms.domain.dto.request.SetMetadataRequestDto;
//import com.sima.dms.domain.dto.response.DocumentOcrProcessDto;
//import com.sima.dms.domain.enums.MetadataFieldNameEnum;
//import com.sima.dms.domain.enums.ProcessStateEnum;
//import com.sima.dms.repository.DocumentOcrRepository;
//import com.sima.dms.repository.DocumentRepository;
//import com.sima.dms.repository.NodeDocumentRepository;
//import com.sima.dms.service.FolderService;
//import com.sima.dms.service.GenericCacheHandler;
//import com.sima.dms.service.NodeDocumentService;
//import com.sima.dms.service.mapper.DocumentOcrMapper;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.rendering.ImageType;
//import org.apache.pdfbox.rendering.PDFRenderer;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.io.InputStream;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//import static com.sima.dms.domain.enums.MetadataFieldNameEnum.date;
//import static com.sima.dms.tools.JalaliCalendar.getBetweenDates;
//import static java.util.Objects.isNull;
//
//@Slf4j
//@Service
//@EnableAsync
////@Transactional
//@AllArgsConstructor
//public class OcrServiceImpl {
//
//    private final GenericCacheHandler genericCacheHandler;
//
//    private final DocumentRepository documentRepository;
//    private final DocumentOcrRepository documentOcrRepository;
//    private final NodeDocumentRepository nodeDocumentRepository;
//
//    private final FolderService folderService;
//    private final NodeDocumentService nodeDocumentService;
//
//    private final DocumentOcrMapper documentOcrMapper;
//
//    @Qualifier("candidateFiles")
//    private final RedisTemplate<String, String> candidateFiles;
//
//    @Qualifier("ocrDocument")
//    private final RedisTemplate<String, DocumentOcrDto> ocrDocument;
//
////    @Qualifier("convertPdfToImage")
////    private final RedisTemplate<String, ConvertDto> convertPdfToImage;
//
//    @Async
//    @Scheduled(fixedDelay = 6000)
//    public void getCandidateFiles() {
//
//        List<String> values = candidateFiles.opsForValue().multiGet(Collections.singleton("candid"));
//        List<String> candidates = documentRepository.ocrCandidate(values);
//
//        if (!isNull(candidates) && !candidates.isEmpty()) {
//            log.info("find candidate documents", candidates);
//            candidateFiles.opsForList().rightPushAll("candid", candidates);
//            documentRepository.updateProcessState(candidates, ProcessStateEnum.PENDING);
//        }
//    }
//
//    @Async
//    @Scheduled(fixedRate = 100)
//    public void ocrDocument() throws IOException {
//
//        String uuid = candidateFiles.opsForList().leftPop("candid");
//
//        if (!isNull(uuid)) {
//
//            Instant startTime = Instant.now();
//
//            InputStream inputStream;
//            DocumentOcrDto documentOcrDto = new DocumentOcrDto(uuid);
//            try {
//                inputStream = nodeDocumentService.getContent(uuid);
//            } catch (Exception e) {
//
//                documentOcrDto.setProcessStateEnum(ProcessStateEnum.GET_CONTENT_ERROR);
//                documentOcrDto.setDescription(e.getMessage());
//                ocrDocument.opsForList().rightPush("handleError", documentOcrDto);
//                log.info("error in get content: " + uuid + ": " + e);
//                return;
//            }
//
//            if (!isNull(inputStream)) {
//
//                String mimeType = nodeDocumentService.getMimeType(uuid);
//
//                if (mimeType.equals("image/tif") || mimeType.equals("image/tiff")) {
//
//                    BufferedImage image = ImageIO.read(inputStream);
//
//                    try {
//                        getDocumentType(image, documentOcrDto);
//                        getDigitsMetadata(image, documentOcrDto);
//                        log.info("finished ocr process for : ", uuid);
//
//                    } catch (Exception e) {
//                        documentOcrDto.setProcessStateEnum(ProcessStateEnum.OCR_IMAGE_ERROR);
//                        documentOcrDto.setDescription(e.getMessage());
//                        ocrDocument.opsForList().rightPush("handleError", documentOcrDto);
//                        log.info("error in ocr process for: " + uuid + ": " + e);
//                        return;
//                    }
//
//                    documentOcrDto.setOcrProcessTime(ChronoUnit.SECONDS.between(startTime, Instant.now()));
//                    ocrDocument.opsForList().rightPush("ocr", documentOcrDto);
//                    log.info(uuid + "pushed to ocrDocument");
//
//                } else if (mimeType.equals("application/pdf")) {
//
//                    BufferedImage image;
//
//                    try {
//
//                        PDDocument document = PDDocument.load(inputStream);
//                        PDFRenderer pdfRenderer = new PDFRenderer(document);
//                        image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
//                        log.info(uuid + " pdf converted to image");
//                        document.close();
//
//                    } catch (Exception e) {
//                        documentOcrDto.setProcessStateEnum(ProcessStateEnum.OCR_PDF_ERROR);
//                        documentOcrDto.setDescription(e.getMessage());
//                        ocrDocument.opsForList().rightPush("handleError", documentOcrDto);
//                        log.info("error in ocr process for: " + uuid + ": " + e);
//                        return;
//                    }
//
//                    try {
//                        getDocumentType(image, documentOcrDto);
//                        getDigitsMetadata(image, documentOcrDto);
//                        log.info("finished ocr process for : " + uuid);
//
//                    } catch (Exception e) {
//                        documentOcrDto.setProcessStateEnum(ProcessStateEnum.OCR_PDF_ERROR);
//                        documentOcrDto.setDescription(e.getMessage());
//                        ocrDocument.opsForList().rightPush("handleError", documentOcrDto);
//                        log.info("error in ocr process for: " + uuid + ": " + e);
//                        return;
//                    }
//
//                    documentOcrDto.setOcrProcessTime(ChronoUnit.SECONDS.between(startTime, Instant.now()));
//                    ocrDocument.opsForList().rightPush("ocr", documentOcrDto);
//                    log.info(uuid + "pushed to ocrDocument");
//
//                }
//                inputStream.close();
//            }
//        }
//    }
//
//    @Async
//    @Scheduled(fixedRate = 110)
//    public void biProcess() {
//
//        DocumentOcrDto documentOcrDto = ocrDocument.opsForList().leftPop("ocr");
//        if (!isNull(documentOcrDto)) {
//            Instant startTime = Instant.now();
//            try {
//                // set metadata
//                DocumentOcrProcessDto ocrDocuments = documentRepository.getOcrDocuments(documentOcrDto.getNodeDocumentUuid());
//                documentOcrDto.setRealDates(getBetweenDates(ocrDocuments.getFromDate(), ocrDocuments.getToDate()));
//                documentOcrDto.setRealBranchCode(String.valueOf(ocrDocuments.getBranchCode()));
//                setOcrMetadata(documentOcrDto);
//            } catch (Exception e) {
//                documentOcrDto.setProcessStateEnum(ProcessStateEnum.OCR_SET_METADATA_ERROR);
//                documentOcrDto.setDescription(e.getMessage());
//                ocrDocument.opsForList().rightPush("handleError", documentOcrDto);
//                log.info("error set metadata for: " + documentOcrDto.getNodeDocumentUuid() + ": " + e);
//                return;
//            }
//
//            try {
//                documentOcrDto.setBiProcessTime(ChronoUnit.SECONDS.between(startTime, Instant.now()));
//                documentRepository.updateProcessState(documentOcrDto.getNodeDocumentUuid(), documentOcrDto.getDescription(), ProcessStateEnum.SUCCESS, documentOcrDto.getOcrProcessTime(), documentOcrDto.getBiProcessTime());
////                nodeDocumentRepository.updateNodeDocument(documentOcrDto.getNodeDocumentUuid(), documentOcrDto.getOcrText(), true);
//                documentOcrRepository.save(documentOcrMapper.toEntity(documentOcrDto));
//                log.info("update process state field for: " + documentOcrDto.getNodeDocumentUuid());
//            } catch (Exception e) {
//                documentOcrDto.setProcessStateEnum(ProcessStateEnum.UPDATE_NODE_DOCUMENT_ERROR);
//                documentOcrDto.setDescription(e.getMessage());
//                ocrDocument.opsForList().rightPush("handleError", documentOcrDto);
//                log.info("error set metadata for: " + documentOcrDto.getNodeDocumentUuid() + ": " + e);
//            }
//        }
//    }
//
//    @Async
//    @Scheduled(fixedRate = 600)
//    public void errorHandler() {
//
//        DocumentOcrDto documentOcrDto = ocrDocument.opsForList().leftPop("handleError");
//
//        if (!isNull(documentOcrDto)) {
//            documentRepository.updateProcessState(documentOcrDto.getNodeDocumentUuid(), documentOcrDto.getDescription(), documentOcrDto.getProcessStateEnum(), documentOcrDto.getOcrProcessTime(), documentOcrDto.getBiProcessTime());
//            nodeDocumentRepository.updateNodeDocument(documentOcrDto.getNodeDocumentUuid(), documentOcrDto.getOcrText(), true);
//        }
//    }
//
////    @Scheduled(cron = "0 29 12 * * *")
////    public void saveUnsuccessfulّFiles() {
////
////        List<String> uuids = documentRepository.findUnsuccessfulّBIFiles(Arrays.asList(INCOMPLETE_DATA, RESULT_NOT_FOUND), toDate(Instant.now()));
////
////        if (!folderService.isValidFolder(rootPath + tempPath))
////            folderService.createFolder(tempPath);
////
////        uuids.forEach(uuid->{
////            nodeDocumentService.extendedCopy(uuid,rootPath+tempPath,uuid,false,false,false,false,false);
////        });
////
////    }
//
//    private void getDocumentType(BufferedImage image, DocumentOcrDto documentOcrDto) throws TesseractException {
//
//        Tesseract tesseract = tesseract(6, "dms-farsi-sf-bank");
//
//        String text = tesseract.doOCR(image);
//
//        String blacklist = "[a-z^A-Z^0-9\\^^^<>$_!@#%&*+=.،,«»۔?؟©۱۲۳۴۵۶۷۸۹۰]+";
//        text = text.replaceAll(blacklist, "");
//
////        TextNormalized.filterSentence(text);
//
//        documentOcrDto.setOcrText(text);
//        List<String> lines = Arrays.stream(text.split("\n")).collect(Collectors.toList());
//        lines = lines.stream().filter(line -> line.length() >= 2).collect(Collectors.toList());
//
//        List<String> result = new ArrayList<>();
//
//        lines.forEach(line -> {
//            String[] split = line.split(" ");
//            String lineText = "";
//            for (int i = 0; i < split.length; i++) {
//                if (split[i] != null && split[i].length() >= 2) lineText = lineText + " " + split[i];
//            }
//            if (lineText != null && lineText.length() >= 2) result.add(lineText);
//        });
//
//        List<String> similarDocumentTypes = genericCacheHandler.getSimilarTitles();
//
//        String jaroWinkler = "";
//        String levenshtein = "";
//        double jaroWinklerDistance = 0;
//        double levenshteinDistance = 0;
//
//        if (!isNull(result) && !result.isEmpty())
//
//            for (int i = 0; i < result.size() / 2; i++) {
//                for (int y = 0; y < similarDocumentTypes.size(); y++) {
//
//                    if (!isNull(result.get(i)) && !result.get(i).isEmpty()) {
//
//                        double newJaroWinklerDistance = getJaroWinklerDistance(result.get(i), similarDocumentTypes.get(y));
//                        double newLevenshteinDistance = getLevenshteinDistance(result.get(i), similarDocumentTypes.get(y));
//
//                        if (newJaroWinklerDistance > jaroWinklerDistance) {
//                            documentOcrDto.setJaroWinklerDistance(newJaroWinklerDistance);
//                            jaroWinklerDistance = newJaroWinklerDistance;
//                            jaroWinkler = similarDocumentTypes.get(y);
//                        }
//                        if (newLevenshteinDistance > levenshteinDistance) {
//                            documentOcrDto.setLevenshteinDistance(newLevenshteinDistance);
//                            levenshteinDistance = newLevenshteinDistance;
//                            levenshtein = similarDocumentTypes.get(y);
//                        }
//                        if (newJaroWinklerDistance >= 0.9 || newLevenshteinDistance >= 0.9) break;
//                    }
//                }
//                if (documentOcrDto.getJaroWinklerDistance() >= 0.9 || documentOcrDto.getLevenshteinDistance() >= 0.9)
//                    break;
//            }
//        String levenshteinDocumentType = documentOcrRepository.getDocumentTitle(jaroWinkler);
//        String jaroWinklerDocumentType = documentOcrRepository.getDocumentTitle(levenshtein);
//
//        if (documentOcrDto.getJaroWinklerDistance() >= 0.6 || documentOcrDto.getLevenshteinDistance() >= 0.5) {
//            if (jaroWinkler.equals(levenshtein)) {
//                documentOcrDto.setTypeMatches(true);
//                documentOcrDto.setDocumentType(levenshteinDocumentType);
////                documentOcrRepository.increaseScore(documentOcrDto.getDocumentType());
//            } else {
//                documentOcrDto.setTypeMatches(true);
//                if (documentOcrDto.getJaroWinklerDistance() > documentOcrDto.getLevenshteinDistance())
//                    documentOcrDto.setDocumentType(levenshteinDocumentType);
//                else documentOcrDto.setDocumentType(jaroWinklerDocumentType);
//            }
//        } else {
//            documentOcrDto.setTypeMatches(false);
//        }
//    }
//
//    private void getDigitsMetadata(BufferedImage image, DocumentOcrDto documentOcrDto) throws TesseractException {
//
//        Tesseract tesseract = new Tesseract();
//        tesseract.setDatapath("tessdata");
//        tesseract.setOcrEngineMode(1);
//        tesseract.setPageSegMode(6);
//        tesseract.setLanguage("dms-mrz-bank");
//        tesseract.setVariable("user_defined_dpi", "300");
//
//        String text = tesseract.doOCR(image);
//
//        String blacklist = "[a-z^A-Z\\^^^<>$_!@#%&*()|+§=”،,«»?؟©]+";
//        text = text.replaceAll(blacklist, "");
//        documentOcrDto.setOcrNumber(text);
//
//        List<String> lines = Arrays.stream(text.split("\n")).collect(Collectors.toList());
//        lines = lines.stream().filter(line -> line.length() >= 3).collect(Collectors.toList());
//
//        List<String> result = new ArrayList<>();
//
//        lines.forEach(line -> {
//            String[] split = line.split(" ");
//            String lineText = "";
//            for (int i = 0; i < split.length; i++) {
//                if (split[i] != null && split[i].length() >= 3) lineText = lineText + " " + split[i];
//            }
//            if (lineText != null && lineText.length() >= 3) result.add(lineText);
//        });
//
//        List<Long> branchCodes = genericCacheHandler.branchCodes();
//
//        Set<String> branchCoeds = new HashSet<>();
//        for (String line : result) {
//            Matcher branchCodeMatcher = Pattern.compile("(^(\\b\\d{3})[.-]\\b)|([  ](\\b\\d{3})\\b)").matcher(line);
//            while (branchCodeMatcher.find()) {
//                String branchCode = branchCodeMatcher.group().replaceAll("[.-]", "");
//                if (branchCodes.contains(Long.valueOf(branchCode.trim()))) {
//                    branchCoeds.add(branchCode.trim());
//                }
//            }
//        }
//        documentOcrDto.setBranchCodes(branchCoeds);
//
//        Set<String> documentNumbers = new HashSet<>();
//        Matcher docNoMatcher = Pattern.compile("(^\\d{7,8}-?\\d?$)").matcher(text);
//        while (docNoMatcher.find()) {
//            String group = docNoMatcher.group();
//            if (group.contains("-") && group.length() == 10) {
//                documentNumbers.add(group.substring(0, 8));
//                documentNumbers.add(group.substring(0, 7) + group.substring(group.length() - 1));
//            } else
//                documentNumbers.add(group);
//        }
//        documentOcrDto.setDocumentNumbers(documentNumbers);
//
//        Set<String> dates = new HashSet<>();
//        Matcher dateMatcher = Pattern.compile("(\\d{4}\\/\\d{2}\\/\\d{2})").matcher(text);
//        while (dateMatcher.find()) {
//            dates.add(dateMatcher.group());
//        }
//        documentOcrDto.setDates(dates);
//    }
//
//    public static double getJaroWinklerDistance(String x, String y) {
//        if (x == null && y == null) {
//            return 1.0;
//        }
//        if (x == null || y == null) {
//            return 0.0;
//        }
//        return StringUtils.getJaroWinklerDistance(x, y);
//    }
//
//    public static double getLevenshteinDistance(String x, String y) {
//
//        double maxLength = Double.max(x.length(), y.length());
//        if (maxLength > 0) {
//            return (maxLength - StringUtils.getLevenshteinDistance(x, y)) / maxLength;
//        }
//        return 1.0;
//    }
//
//    private void setOcrMetadata(DocumentOcrDto documentOcrDto) {
//
//        MetadataDto documentDate = new MetadataDto(date, documentOcrDto.getDates().toString());
//        MetadataDto branchCode = new MetadataDto(MetadataFieldNameEnum.branchCode, documentOcrDto.getBranchCodes().toString());
//        MetadataDto documentNo = new MetadataDto(MetadataFieldNameEnum.documentNo, documentOcrDto.getDocumentNumbers().toString());
//        MetadataDto documentDescription = new MetadataDto(MetadataFieldNameEnum.documentDescription, documentOcrDto.getDocumentType());
//        MetadataDto levenshteinDistance = new MetadataDto(MetadataFieldNameEnum.levenshteinDistance, String.valueOf(documentOcrDto.getLevenshteinDistance()));
//        MetadataDto jaroWinklerDistance = new MetadataDto(MetadataFieldNameEnum.jaroWinklerDistance, String.valueOf(documentOcrDto.getJaroWinklerDistance()));
//        List<MetadataDto> metadata = Arrays.asList(documentNo, branchCode, documentDescription, documentDate, levenshteinDistance, jaroWinklerDistance);
//        SetMetadataRequestDto setMetadataRequestDto = new SetMetadataRequestDto(documentOcrDto.getNodeDocumentUuid(), metadata);
//        nodeDocumentService.setMetadata(setMetadataRequestDto);
//    }
//
//    private Tesseract tesseract(int psm, String language) {
//        Tesseract tesseract = new Tesseract();
//        tesseract.setDatapath("tessdata");
//        tesseract.setOcrEngineMode(1);
//        tesseract.setPageSegMode(psm);
//        tesseract.setLanguage(language);
//        tesseract.setVariable("user_defined_dpi", "300");
////      tesseract.setConfigs(Arrays.asList(config));
//        return tesseract;
//    }
//
//}
