package com.sima.dms.tools;

import com.sima.dms.domain.dto.NodePropertyDto;
import com.sima.dms.domain.dto.document.DocumentConflictDto;
import com.sima.dms.domain.dto.document.DocumentDto;
import com.sima.dms.domain.dto.baseinformation.ConflictReasonDto;
import com.sima.dms.domain.dto.request.AdvanceDocumentSearchDto;
import com.sima.dms.domain.entity.baseinformation.ConflictReason;
import com.sima.dms.domain.enums.ConflictTypeEnum;
import com.sima.dms.repository.ConflictReasonRepository;
import com.sima.dms.service.DocumentService;
import lombok.AllArgsConstructor;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class DocumentExcelGenerator {

    private final DocumentService documentService;
    private final ConflictReasonRepository conflictReasonRepository;
    private static final List<String> documentHeaders =
            Arrays.asList(
                    "کد و نام واحد بانکی",
//                    "شماره سند",
//                    "طبقه سند",
//                    "شرح سند",
//                    "تاریخ سند",
                    "کد نگهداری",
                    "وضعیت",
                    "تاریخ ثبت",
                    "تاریخ دسته اسناد از",
                    "تاریخ دسته اسناد تا",
                    "تاریخ ارسال",
                    "شماره ردیف دسته اسناد",
                    "نام فایل"
            );


    private static void setHeader(XSSFSheet sheet, List<String> headers) {

        Row row = sheet.createRow(0);
        CellStyle style = createStyle(sheet.getWorkbook(), 14, false);
        style.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.DIAMONDS);

        for (int i = 0; i < headers.size(); i++) {
            sheet.setColumnWidth(i, 25 * 200);
        }

        for (int i = 0; i < headers.size(); i++) {
            createCell(i, row, headers.get(i), style);
        }
    }

    private static void createCell(int columnNumber, Row row, Object value, CellStyle style) {
        Cell cell = row.createCell(columnNumber);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(String.valueOf(value));
        }
        cell.setCellStyle(style);
    }

    private static CellStyle createStyle(XSSFWorkbook workbook, double height, boolean bold) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = new XSSFFont();
        font.setBold(bold);
        font.setFontHeight(height);
        style.setFont(font);
        return style;
    }

    private static void write(List<DocumentDto> documentDtos, XSSFSheet sheet) {
        try {
            int rowNumber = 1;
            CellStyle style = createStyle(sheet.getWorkbook(), 14, false);

            for (DocumentDto record : documentDtos) {
                int columnNumber = 0;
                String branchName = record.getBranchName() != null ? record.getBranchName() : "";
                Row row = sheet.createRow(rowNumber++);
                createCell(columnNumber++, row, record.getBranchCode() != null ? record.getBranchCode() + "-" + branchName : "" + branchName, style);
//                createCell(columnNumber++, row, record.getFile().getProperties().stream().filter(nodePropertyDto -> nodePropertyDto.getName().equals("okp:Accounting.docNo")).map(NodePropertyDto::getValue).findFirst().get(), style);
//                createCell(columnNumber++, row, record.getFile().getProperties().stream().filter(nodePropertyDto -> nodePropertyDto.getName().equals("okp:Accounting.documentClass")).map(NodePropertyDto::getValue).findFirst().get(), style);
//                createCell(columnNumber++, row, record.getFile().getProperties().stream().filter(nodePropertyDto -> nodePropertyDto.getName().equals("okp:Accounting.documentDescription")).map(NodePropertyDto::getValue).findFirst().get(), style);
//                createCell(columnNumber++, row, record.getFile().getProperties().stream().filter(nodePropertyDto -> nodePropertyDto.getName().equals("okp:Accounting.date")).map(NodePropertyDto::getValue).findFirst().get(), style);
                createCell(columnNumber++, row, record.getMaintenanceCode() != null ? record.getMaintenanceCode() : "", style);
                createCell(columnNumber++, row, record.getState().getName().getPersianName(), style);
                createCell(columnNumber++, row, jalaliDate(record.getRegisterDate()), style);
                createCell(columnNumber++, row, jalaliDate(record.getFromDate()), style);
                createCell(columnNumber++, row, jalaliDate(record.getToDate()), style);
                createCell(columnNumber++, row, record.getSendDate() != null ? jalaliDate(record.getSendDate()) : "", style);
                createCell(columnNumber++, row, record.getDocumentSetRowsNumber(), style);
                createCell(columnNumber++, row, record.getFile().getName(), style);

            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private static String jalaliDate(Instant date) {
        if (date != null)
            return JalaliCalendar.getJalaliDate(Date.from(date));
        return "";
    }

    public void generate(AdvanceDocumentSearchDto searchDto, HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("document");
        List<DocumentDto> documentDtos = documentService.advanceSearch(
                searchDto.getMaintenanceCode(),
                searchDto.getStates(),
                searchDto.getFromDate(),
                searchDto.getToDate(),
                searchDto.getRegisterFromDate(),
                searchDto.getRegisterToDate(),
                searchDto.getBranchIds(),
                searchDto.getFilename(),
                searchDto.getDocumentNumber(),
                searchDto.getDocumentDate(),
                searchDto.getReason(),
                searchDto.getRowNumber(), searchDto.getType(),
                searchDto.getCustomerNumber(),
                searchDto.getFileNumber(),
                searchDto.getFileStatusId(),
                searchDto.getFileTypeId(),
                null
        ).toList();
        sheet.autoSizeColumn(0);
        sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);
        setHeader(sheet, documentHeaders);
        write(documentDtos, sheet);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


    private void setHeaderReport(XSSFSheet sheet, List<String> headers, int firstCol, int lastCol) throws IOException {

        Row row = sheet.createRow(0);
        XSSFCellStyle style = (XSSFCellStyle) createStyle(sheet.getWorkbook(), 50, true);
        IndexedColorMap colorMap = sheet.getWorkbook().getStylesSource().getIndexedColors();
        XSSFColor color = new XSSFColor(new java.awt.Color(237, 224, 234), colorMap);
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeight((short) (12 * 20));
        font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());

        style.setFont(font);

        for (int i = 0; i < headers.size(); i++) {
            sheet.setColumnWidth(i, 30 * 200);
        }

        for (int i = 0; i < headers.size(); i++) {
            if (i == firstCol) {
                style.setBorderBottom(BorderStyle.MEDIUM);
                style.setBorderLeft(BorderStyle.MEDIUM);
                style.setBorderRight(BorderStyle.MEDIUM);
                createCell(i, row, "شرح مغایرت", style);
            } else
                createCell(i, row, "", style);

        }
        Row row1 = sheet.createRow(1);
        for (int i = 0; i < headers.size(); i++) {
//            if (i >= 13 && i <= 23) {
//                style.setRotation((short) 90);
//            }
            createCell(i, row1, headers.get(i), style);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, firstCol, lastCol));
        sheet.createFreezePane(0, 2);
    }

    private void writeReport(List<DocumentDto> documents, XSSFSheet sheet, List<String> conflictReasonName) {

        int rowNumber = 2;

        CellStyle style = createStyle(sheet.getWorkbook(), 14, false);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        style.setWrapText(true);
        for (DocumentDto record : documents) {
            AtomicInteger columnNumber = new AtomicInteger();
            Row row = sheet.createRow(rowNumber++);
            createCell(columnNumber.getAndIncrement(), row, String.valueOf(rowNumber - 2), style);
            createCell(columnNumber.getAndIncrement(), row, record.getBranchName(), style);
            createCell(columnNumber.getAndIncrement(), row, record.getBranchCode(), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getFromDate()), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getToDate()), style);
            createCell(columnNumber.getAndIncrement(), row, record.getType().getPersianName() + "(" + record.getType().getCode() + ")", style);
            createCell(columnNumber.getAndIncrement(), row, record.getFileTypeTitle() != null ? record.getFileTypeTitle() : "", style);
            createCell(columnNumber.getAndIncrement(), row, record.getFileStatusTitle() != null ? record.getFileStatusTitle() : "", style);
            createCell(columnNumber.getAndIncrement(), row, record.getCustomerNumber() != null ? record.getCustomerNumber() : "", style);
            createCell(columnNumber.getAndIncrement(), row, record.getFileNumber() != null ? record.getFileNumber() : "", style);
            createCell(columnNumber.getAndIncrement(), row, record.getMaintenanceCode(), style);
            createCell(columnNumber.getAndIncrement(), row, record.getDocumentSetRowsNumber(), style);
            createCell(columnNumber.getAndIncrement(), row, record.getState().getName().getPersianName(), style);
            createCell(columnNumber.getAndIncrement(), row, record.getScannerName() != null ? record.getScannerName() : "", style);
            createCell(columnNumber.getAndIncrement(), row, !record.getFile().getProperties().isEmpty() ?
                    record.getFile().getProperties().stream().filter(nodePropertyDto -> nodePropertyDto.getName().equals("date")).map(NodePropertyDto::getValue).collect(Collectors.toList()).toString().replace("[", "").replace("]", "") : "", style);
            createCell(columnNumber.getAndIncrement(), row, !record.getFile().getProperties().isEmpty() ?
                    record.getFile().getProperties().stream().filter(nodePropertyDto -> nodePropertyDto.getName().equals("documentNo")).map(NodePropertyDto::getValue).collect(Collectors.toList()).toString().replace("[", "").replace("]", "") : "", style);

            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getRegisterDate()), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getOcrFinishedTime()), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getSentConflictDate()), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getPrimaryConfirmedDate()), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getFixConflictDate()), style);
            List<DocumentConflictDto> documentConflictDtos = record.getConflicts();
            if (documentConflictDtos != null && !documentConflictDtos.isEmpty()) {
                DocumentConflictDto documentConflictDto = documentConflictDtos.get(documentConflictDtos.size() - 1);
                List<String> conflictReasons = documentConflictDto.getConflictReasons().stream().map(ConflictReasonDto::getReason).collect(Collectors.toList());
                conflictReasonName.forEach(s -> {
                    if (conflictReasons.contains(s)) {
                        char st = '\u2713';
                        createCell(columnNumber.getAndIncrement(), row, String.valueOf(st), style);
                    } else
                        createCell(columnNumber.getAndIncrement(), row, "", style);
                });
                createCell(columnNumber.getAndIncrement(), row, conflictReasons.size(), style);
                createCell(columnNumber.getAndIncrement(), row, documentConflictDto.getRegisterDescription() != null ? documentConflictDto.getRegisterDescription() : "", style);
            }
        }
    }

    public void generateReport(AdvanceDocumentSearchDto searchDto, HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("documentReport");
        List<DocumentDto> documentDtos = documentService.advanceSearch(
                searchDto.getMaintenanceCode(),
                searchDto.getStates(),
                searchDto.getFromDate(),
                searchDto.getToDate(),
                searchDto.getRegisterFromDate(),
                searchDto.getRegisterToDate(),
                searchDto.getBranchIds(),
                searchDto.getFilename(),
                searchDto.getDocumentNumber(),
                searchDto.getDocumentDate(),
                searchDto.getReason(),
                searchDto.getRowNumber(),
                searchDto.getType(),
                searchDto.getCustomerNumber(),
                searchDto.getFileNumber(),
                searchDto.getFileStatusId(),
                searchDto.getFileTypeId(),
                null
        ).toList();
        sheet.autoSizeColumn(0);
        sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

        List<String> documentReportHeaders = new ArrayList<>();
        documentReportHeaders.add("ردیف");
        documentReportHeaders.add("نام واحد بانکی(شعبه/مرکزخدمات/باجه/دفتر)");
        documentReportHeaders.add("کد  واحد بانکی(شعبه/مرکزخدمات/باجه/دفتر)");
        documentReportHeaders.add("تاریخ دسته اسناد از");
        documentReportHeaders.add("تاریخ دسته اسناد تا");
        documentReportHeaders.add("نوع و کد دسته اسناد");
        documentReportHeaders.add("نوع پرونده");
        documentReportHeaders.add("وضعیت پرونده");
        documentReportHeaders.add("شماره مشتری");
        documentReportHeaders.add("شماره پرونده");
        documentReportHeaders.add("کد نگهداری");
        documentReportHeaders.add("شماره ردیف دسته سند");
        documentReportHeaders.add("وضعیت اسناد");
        documentReportHeaders.add("نام و نام خانوادگی کاربر اداره اسناد");
        documentReportHeaders.add("تاریخ سند");
        documentReportHeaders.add("شماره سند");
        documentReportHeaders.add("تاريخ اسكن (بارگذاری) شده");
        documentReportHeaders.add("تاریخ پردازش شده");
        documentReportHeaders.add("تاریخ دارای مغایرت ارسال شده (دارای مغایرت)");
        documentReportHeaders.add("تاریخ تایید اولیه");
        documentReportHeaders.add("تاريخ رفع مغايرت فايل");

        List<String> conflictReasonName = conflictReasonRepository.getAllByType(ConflictTypeEnum.DOCUMENT).stream().map(ConflictReason::getReason).collect(Collectors.toList());
        documentReportHeaders.addAll(conflictReasonName);
        documentReportHeaders.add("مجموع تعداد مغایرت ها");
        documentReportHeaders.add("توضیحات");

        setHeaderReport(sheet, documentReportHeaders, documentReportHeaders.size() - conflictReasonName.size() - 2, documentReportHeaders.size() - 3);
        writeReport(documentDtos, sheet, conflictReasonName);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
