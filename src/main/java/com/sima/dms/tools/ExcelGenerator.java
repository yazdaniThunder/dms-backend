package com.sima.dms.tools;

import com.sima.dms.domain.dto.baseinformation.ConflictReasonDto;
import com.sima.dms.domain.dto.documentSet.DocumentSetConflictDto;
import com.sima.dms.domain.dto.documentSet.DocumentSetDto;
import com.sima.dms.domain.dto.request.AdvanceDocumentSetSearchDto;
import com.sima.dms.domain.entity.baseinformation.ConflictReason;
import com.sima.dms.domain.enums.ConflictTypeEnum;
import com.sima.dms.repository.ConflictReasonRepository;
import com.sima.dms.service.DocumentSetService;
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
public class ExcelGenerator {
    private final DocumentSetService documentSetService;
    private final ConflictReasonRepository conflictReasonRepository;
    private static final List<String> documentSetHeaders =
            Arrays.asList(
                    "ردیف",
                    "شماره ردیف ددسته سند",
                    "تاریخ ثبت دسته اسناد",
                    "تاریخ دسته اسناد از",
                    "تاریخ دسته اسناد تا",
                    "تاریخ ارسال",
                    " کد واحد بانکی",
                    " نام واحد بانکی",
                    "کابر ثبت کننده",
                    "کد ثبت",
                    "نوع دسته اسناد",
                    "نوع پرونده",
                    "وضعیت پرونده",
                    "شماره مشتری",
                    "شماره پرونده",
                    "وضعبت");

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

    private static void setHeader(XSSFSheet sheet, List<String> headers) {

        Row row = sheet.createRow(0);
        CellStyle style = createStyle(sheet.getWorkbook(), 14, false);
        style.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
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

    private void write(List<DocumentSetDto> documentSets, XSSFSheet sheet) {

        int rowNumber = 1;

        CellStyle style = createStyle(sheet.getWorkbook(), 14, false);

        for (DocumentSetDto record : documentSets) {
            int columnNumber = 0;
            Row row = sheet.createRow(rowNumber++);
            createCell(columnNumber++, row, String.valueOf(rowNumber - 1), style);
            createCell(columnNumber++, row, record.getRowsNumber(), style);
            createCell(columnNumber++, row, jalaliDate(record.getRegisterDate()), style);
            createCell(columnNumber++, row, jalaliDate(record.getFromDate()), style);
            createCell(columnNumber++, row, jalaliDate(record.getToDate()), style);
            createCell(columnNumber++, row, jalaliDate(record.getSendDate()), style);
            createCell(columnNumber++, row, record.getBranch().getBranchCode(), style);
            createCell(columnNumber++, row, record.getBranch().getBranchName(), style);
            createCell(columnNumber++, row, record.getConfirmerName(), style);
            createCell(columnNumber++, row, record.getConfirmerName(), style);
            createCell(columnNumber++, row, record.getType().getPersianName(), style);
            createCell(columnNumber++, row, record.getFileTypeTitle() != null ? record.getFileTypeTitle() : "", style);
            createCell(columnNumber++, row, record.getFileStatusTitle() != null ? record.getFileStatusTitle() : "", style);
            createCell(columnNumber++, row, record.getCustomerNumber() != null ? record.getCustomerNumber() : "", style);
            createCell(columnNumber++, row, record.getFileNumber() != null ? record.getFileNumber() : "", style);
            createCell(columnNumber++, row, record.getState().getName().getPersianName(), style);
        }
    }

    private void writeReport(List<DocumentSetDto> documentSets, XSSFSheet sheet, List<String> conflictReasonName) {

        int rowNumber = 2;

        CellStyle style = createStyle(sheet.getWorkbook(), 14, false);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        style.setWrapText(true);
        for (DocumentSetDto record : documentSets) {
            AtomicInteger columnNumber = new AtomicInteger();
            Row row = sheet.createRow(rowNumber++);
            createCell(columnNumber.getAndIncrement(), row, String.valueOf(rowNumber - 2), style);
            createCell(columnNumber.getAndIncrement(), row, record.getBranch().getBranchName(), style);
            createCell(columnNumber.getAndIncrement(), row, record.getBranch().getBranchCode(), style);
            createCell(columnNumber.getAndIncrement(), row, record.getRowsNumber() + record.getSequence(), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getFromDate()), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getToDate()), style);
            createCell(columnNumber.getAndIncrement(), row, record.getType().getPersianName() + "(" + record.getType().getCode() + ")", style);
            createCell(columnNumber.getAndIncrement(), row, record.getState().getName().getPersianName(), style);
            createCell(columnNumber.getAndIncrement(), row, record.getFileTypeTitle() != null ? record.getFileTypeTitle() : "", style);
            createCell(columnNumber.getAndIncrement(), row, record.getFileStatusTitle() != null ? record.getFileStatusTitle() : "", style);
            createCell(columnNumber.getAndIncrement(), row, record.getCustomerNumber() != null ? record.getCustomerNumber() : "", style);
            createCell(columnNumber.getAndIncrement(), row, record.getFileNumber() != null ? record.getFileNumber() : "", style);
            createCell(columnNumber.getAndIncrement(), row, record.getRegistrarName(), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getRegisterDate()), style);
            createCell(columnNumber.getAndIncrement(), row, record.getConfirmerName() != null ? record.getConfirmerName() : "", style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getSendDate()), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getSendDate()), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getPrimaryConfirmedDate()), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getConflictingDate()), style);
            createCell(columnNumber.getAndIncrement(), row, jalaliDate(record.getFixConflictDate()), style);
            List<DocumentSetConflictDto> documentSetConflictDtos = record.getConflicts();
            if (documentSetConflictDtos != null && !documentSetConflictDtos.isEmpty()) {
                DocumentSetConflictDto documentSetConflictDto = documentSetConflictDtos.get(documentSetConflictDtos.size() - 1);
                List<String> conflictReasons = documentSetConflictDto.getConflictReasons().stream().map(ConflictReasonDto::getReason).collect(Collectors.toList());
                conflictReasonName.forEach(s -> {
                    if (conflictReasons.contains(s)) {
                        char st = '\u2713';
                        createCell(columnNumber.getAndIncrement(), row, String.valueOf(st), style);
                    } else
                        createCell(columnNumber.getAndIncrement(), row, "", style);
                });
                createCell(columnNumber.getAndIncrement(), row, documentSetConflictDto.getRegisterDescription() != null ? documentSetConflictDto.getRegisterDescription() : "", style);
            }
        }
    }

    private static String jalaliDate(Instant date) {
        if (date != null)
            return JalaliCalendar.getJalaliDate(Date.from(date));
        return "";
    }


    public void generateReport(AdvanceDocumentSetSearchDto searchDto, HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("documentSetReport");
        List<DocumentSetDto> documentSets = documentSetService.advanceSearch(
                searchDto.getType(),
                searchDto.getStatus(),
                searchDto.getFromDate(),
                searchDto.getToDate(),
                searchDto.getRegisterFromDate(),
                searchDto.getRegisterToDate(),
                searchDto.getSentFromDate(),
                searchDto.getSentToDate(),
                searchDto.getRegistrarId(),
                searchDto.getConfirmerId(),
                searchDto.getScannerId(),
                searchDto.getRowNumber(),
                searchDto.getBranchIds(),
                searchDto.getReason(),
                searchDto.getCustomerNumber(),
                searchDto.getFileNumber(),
                searchDto.getFileStatusId(),
                searchDto.getFileTypeId(),
                null
        ).toList();
        sheet.autoSizeColumn(0);
        sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);

        List<String> documentSetReportHeaders = new ArrayList<>();
        documentSetReportHeaders.add("ردیف");
        documentSetReportHeaders.add("نام واحد بانکی(شعبه/مرکزخدمات/باجه/دفتر)");
        documentSetReportHeaders.add("کد  واحد بانکی(شعبه/مرکزخدمات/باجه/دفتر)");
        documentSetReportHeaders.add("شماره ردیف دسته سند");
        documentSetReportHeaders.add("تاریخ دسته اسناد از");
        documentSetReportHeaders.add("تاریخ دسته اسناد تا");
        documentSetReportHeaders.add("نوع و کد دسته اسناد");
        documentSetReportHeaders.add("وضعیت دسته اسناد");
        documentSetReportHeaders.add("نوع پرونده");
        documentSetReportHeaders.add("وضعیت پرونده");
        documentSetReportHeaders.add("شماره مشتری");
        documentSetReportHeaders.add("شماره پرونده");
        documentSetReportHeaders.add("نام و نام خانوادگی کاربر ثبت کننده");
        documentSetReportHeaders.add("تاریخ ثبت دسته اسناد");
        documentSetReportHeaders.add("نام و نام خانوادگی کاربر تاییدکننده");
        documentSetReportHeaders.add("تاریخ تایید دسته اسناد");
        documentSetReportHeaders.add("تاریخ ارسال دسته اسناد");
        documentSetReportHeaders.add("تاریخ تایید شده اولیه دسته اسناد");
        documentSetReportHeaders.add("تاریخ ثبت مغایرت دسته اسناد");
        documentSetReportHeaders.add("تاریخ رفع مغایرت دسته اسناد");

        List<String> conflictReasonName = conflictReasonRepository.getAllByType(ConflictTypeEnum.DOCUMENT_SET).stream().map(ConflictReason::getReason).collect(Collectors.toList());
        documentSetReportHeaders.addAll(conflictReasonName);
        documentSetReportHeaders.add("توضیحات");

        setHeaderReport(sheet, documentSetReportHeaders, documentSetReportHeaders.size() - conflictReasonName.size() - 1, documentSetReportHeaders.size() - 2);
        writeReport(documentSets, sheet, conflictReasonName);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void generate(AdvanceDocumentSetSearchDto searchDto, HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("documentSet");
        List<DocumentSetDto> documentSets = documentSetService.advanceSearch(
                searchDto.getType(),
                searchDto.getStatus(),
                searchDto.getFromDate(),
                searchDto.getToDate(),
                searchDto.getRegisterFromDate(),
                searchDto.getRegisterToDate(),
                searchDto.getSentFromDate(),
                searchDto.getSentToDate(),
                searchDto.getRegistrarId(),
                searchDto.getConfirmerId(),
                searchDto.getScannerId(),
                searchDto.getRowNumber(),
                searchDto.getBranchIds(),
                searchDto.getReason(),
                searchDto.getCustomerNumber(),
                searchDto.getFileNumber(),
                searchDto.getFileStatusId(),
                searchDto.getFileTypeId(),
                null
        ).toList();
        sheet.autoSizeColumn(0);
        sheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setRightToLeft(true);
        setHeader(sheet, documentSetHeaders);
        write(documentSets, sheet);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
