package com.vavilon.debitcredit.services;

import com.vavilon.debitcredit.configs.MyCompanyProperties;
import com.vavilon.debitcredit.entities.Company;
import com.vavilon.debitcredit.entities.CompanyAccountOperation;
import com.vavilon.debitcredit.entities.dtos.IntervalDate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExcelService {
    private final String TITLE_STYLE = "title";
    private final String PERIOD_STYLE = "period";
    private final String AGREEMENT_STYLE = "agreement";
    private final String TABLE_TITLE_STYLE = "tableTitle";
    private final String TABLE_HEADER_STYLE = "headerTableTitle";
    private final String TABLE_SALDO_STYLE = "saldoTable";
    private final String TABLE_OPERATION_ROW_STYLE = "operationRowTable";
    private final String TABLE_OPERATION_DATE_ROW_STYLE = "operationDateRowTable";
    private final String CALCULUS_TITLE_STYLE = "calculusTitle";
    private final String CALCULUS_POST_TITLE_STYLE = "calculusPostTitle";
    private final String SIGNATURE_STYLE = "signature";

    private final MyCompanyProperties myCompanyProperties;
    private final CompanyService companyService;

    public ExcelService(MyCompanyProperties myCompanyProperties, CompanyService companyService) {
        this.myCompanyProperties = myCompanyProperties;
        this.companyService = companyService;
    }

    public void generateReconciliationReport(Company company, IntervalDate intervalDate,
                                             OutputStream stream) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Акт сверки");
        Map<String, CellStyle> styles = this.createStyles(wb);
        List<CompanyAccountOperation> companyAccountOperationList = companyService
                .getCompanyAccountOperationList(company, intervalDate);

        if (companyAccountOperationList.size() != 0) {
            this.createTitle(sheet, styles.get(TITLE_STYLE));
            this.createDateReconciliationReport(wb, sheet, styles.get(PERIOD_STYLE), company, intervalDate);
            this.createAgreement(sheet, styles.get(AGREEMENT_STYLE), company);
            this.createTable(wb, sheet, company, companyAccountOperationList, styles);
            this.createCalculus(sheet, styles, companyAccountOperationList, company, intervalDate);
            this.createSignature(sheet, styles.get(SIGNATURE_STYLE),
                    14 + companyAccountOperationList.size());
        }

        wb.write(stream);
        wb.close();
    }

    private void createTitle(Sheet sheet, CellStyle cellStyle) {
        Row row = sheet.createRow(1);
        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Акт сверки");

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 14));
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
    }

    private void createDateReconciliationReport(Workbook wb, Sheet sheet, CellStyle cellStyle, Company company,
                                                IntervalDate intervalDate) {
        Row row = sheet.createRow(2);
        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        row.setHeightInPoints(38);
        CreationHelper createHelper = wb.getCreationHelper();
        cell.setCellValue(createHelper.createRichTextString(
                String.format("""
                                взаимных расчетов за период: %s - %s
                                между %s (ИНН %s)
                                и %s (ИНН %s)""",
                        new SimpleDateFormat("dd.MM.yyyy").format(intervalDate.getDateStart()),
                        new SimpleDateFormat("dd.MM.yyyy").format(intervalDate.getDateEnd()),
                        myCompanyProperties.getMyCompanyName(),
                        myCompanyProperties.getMyCompanyINN(),
                        company.getName(),
                        company.getInn())
        ));

        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 14));
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
    }

    private void createAgreement(Sheet sheet, CellStyle cellStyle, Company company) {
        Row row = sheet.createRow(4);
        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        row.setHeightInPoints(40);
        cell.setCellValue(String.format("Мы, нижеподписавшиеся, Директор %s " +
                        "%s, с одной стороны, и ________________ " +
                        "%s _______________________, с другой стороны, составили настоящий акт сверки в том, " +
                        "что состояние взаимных расчетов по данным учета следующее:",
                myCompanyProperties.getMyCompanyName(),
                myCompanyProperties.getDirectorFullName(),
                company.getName()));

        sheet.addMergedRegion(new CellRangeAddress(4, 4, 1, 14));
        CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);
    }

    private void createTable(Workbook wb, Sheet sheet, Company company,
                             List<CompanyAccountOperation> companyAccountOperationList, Map<String, CellStyle> styles) {
        this.createTableTitle(sheet, styles.get(TABLE_TITLE_STYLE));
        this.createTableHeaders(sheet, styles.get(TABLE_HEADER_STYLE));
        this.createTableSaldoBegin(sheet, styles.get(TABLE_SALDO_STYLE), companyAccountOperationList);
        this.createTableOperationRows(sheet, styles, companyAccountOperationList);
        this.createTableSaldoEnd(sheet, styles.get(TABLE_SALDO_STYLE), companyAccountOperationList);

        // Autosize column
        sheet.setColumnWidth(2, 30 * 256);

        // Create border
        PropertyTemplate pt = new PropertyTemplate();
        pt.drawBorders(new CellRangeAddress(6, 9 + companyAccountOperationList.size(), 1, 7),
                BorderStyle.THIN, BorderExtent.ALL);
        pt.applyBorders(sheet);
    }

    private void createTableTitle(Sheet sheet, CellStyle cellStyle) {
        Row row = sheet.createRow(6);
        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(String.format("По данным %s,  Руб", myCompanyProperties.getMyCompanyName()));

        sheet.addMergedRegion(new CellRangeAddress(6, 6, 1, 7));
        CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);
    }

    private void createTableHeaders(Sheet sheet, CellStyle cellStyle) {
        Row row = sheet.createRow(7);
        Cell cellDate = row.createCell(1);
        Cell cellDocument = row.createCell(2);
        Cell cellDebit = row.createCell(4);
        Cell cellCredit = row.createCell(6);

        // Set styles
        cellDate.setCellStyle(cellStyle);
        cellDocument.setCellStyle(cellStyle);
        cellDebit.setCellStyle(cellStyle);
        cellCredit.setCellStyle(cellStyle);

        // Set values
        cellDate.setCellValue("Дата");
        cellDocument.setCellValue("Документ");
        cellDebit.setCellValue("Дебет");
        cellCredit.setCellValue("Кредит");

        // Set merged region
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 2, 3));
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 4, 5));
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 6, 7));
    }

    private void createTableSaldoBegin(Sheet sheet, CellStyle cellStyle,
                                       List<CompanyAccountOperation> companyAccountOperationList) {
        CompanyAccountOperation firstOperation = companyAccountOperationList.get(companyAccountOperationList.size() - 1);
        BigDecimal saldoBegin = firstOperation
                .getCurrentBalance()
                .add(firstOperation
                        .getAccountOperation()
                        .negate())
                .setScale(2, RoundingMode.DOWN);

        this.createSaldo(sheet, cellStyle,
                8,
                "Сальдо начальное",
                saldoBegin);
    }

    private void createTableSaldoEnd(Sheet sheet, CellStyle cellStyle, List<CompanyAccountOperation> companyAccountOperationList) {
        this.createSaldo(sheet, cellStyle,
                9 + companyAccountOperationList.size(),
                "Сальдо конечное",
                companyAccountOperationList.get(0).getCurrentBalance());
    }

    private void createSaldo(Sheet sheet, CellStyle cellStyle, int numberOfRow,
                             String title,
                             BigDecimal saldo) {
        Row row = sheet.createRow(numberOfRow);
        Cell cellTitle = row.createCell(1);
        Cell cellDebit = row.createCell(4);
        Cell cellCredit = row.createCell(6);

        // Set styles
        cellTitle.setCellStyle(cellStyle);
        cellDebit.setCellStyle(cellStyle);
        cellCredit.setCellStyle(cellStyle);

        // Set values
        cellTitle.setCellValue(title);

        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            cellDebit.setCellValue(NumberFormat.getCurrencyInstance().format(saldo));
        } else if (saldo.compareTo(BigDecimal.ZERO) != 0){
            cellCredit.setCellValue(NumberFormat.getCurrencyInstance().format(saldo.negate()));
        }


        // Set merged region
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow, 1, 3));
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow, 4, 5));
        sheet.addMergedRegion(new CellRangeAddress(numberOfRow, numberOfRow, 6, 7));
    }

    private void createTableOperationRows(Sheet sheet,
                                          Map<String, CellStyle> styles,
                                          List<CompanyAccountOperation> companyAccountOperationList) {
        for (int i = companyAccountOperationList.size() - 1; i >= 0; i--) {
            Row row = sheet.createRow(companyAccountOperationList.size() - i + 8);
            this.createTableOperationRow(
                    sheet,
                    styles,
                    row,
                    companyAccountOperationList.get(i));
        }
    }

    private void createTableOperationRow(Sheet sheet, Map<String, CellStyle> styles, Row row,
                                         CompanyAccountOperation companyAccountOperation) {
        // Create cells
        Cell cellDate = row.createCell(1);
        Cell cellDocument = row.createCell(2);
        Cell cellDebit = row.createCell(4);
        Cell cellCredit = row.createCell(6);

        // Set styles
        cellDate.setCellStyle(styles.get(TABLE_OPERATION_DATE_ROW_STYLE));
        cellDocument.setCellStyle(styles.get(TABLE_OPERATION_ROW_STYLE));
        cellDebit.setCellStyle(styles.get(TABLE_OPERATION_ROW_STYLE));
        cellCredit.setCellStyle(styles.get(TABLE_OPERATION_ROW_STYLE));

        // Set values
        cellDate.setCellValue(companyAccountOperation.getDocumentDate());
        cellDocument.setCellValue(companyAccountOperation.getDocumentName());
        if (companyAccountOperation.getIsDebit()) {
            cellDebit.setCellValue(companyAccountOperation.getFormatAccountBalance());
            cellCredit.setCellValue("");
        } else {
            cellDebit.setCellValue("");
            cellCredit.setCellValue(companyAccountOperation.getFormatAccountBalance());
        }

        // Set merged region
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 5));
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 7));
    }

    private void createCalculus(Sheet sheet, Map<String, CellStyle> styles,
                                List<CompanyAccountOperation> companyAccountOperationList,
                                Company company,
                                IntervalDate intervalDate) {
        Row rowTitle = sheet.createRow(11 + companyAccountOperationList.size());
        Row rowCalculus = sheet.createRow(12 + companyAccountOperationList.size());

        rowCalculus.setHeightInPoints(33);
        Cell cellTitle = rowTitle.createCell(1);
        Cell cellCalculus = rowCalculus.createCell(1);

        // Cell styles
        cellTitle.setCellStyle(styles.get(CALCULUS_TITLE_STYLE));
        cellCalculus.setCellStyle(styles.get(CALCULUS_POST_TITLE_STYLE));

        // Calculus summ and find company name
        String companyName = myCompanyProperties.getMyCompanyName();
        BigDecimal currentBalance = BigDecimal.ZERO;

        if (companyAccountOperationList.size() != 0) {
            currentBalance = companyAccountOperationList.get(0).getCurrentBalance();
            if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
                companyName = company.getName();
                currentBalance = currentBalance.negate();
            }

        } else {
            companyName = "";
        }

        cellTitle.setCellValue(String.format("По данным %s", myCompanyProperties.getMyCompanyName()));
        cellCalculus.setCellValue(String.format("на %s задолженность в пользу %s %s.",
                new SimpleDateFormat("dd.MM.yyyy").format(intervalDate.getDateEnd()),
                companyName,
                NumberFormat.getCurrencyInstance().format(currentBalance)
        ));

        sheet.addMergedRegion(new CellRangeAddress(rowTitle.getRowNum(), rowTitle.getRowNum(), 1, 6));
        sheet.addMergedRegion(new CellRangeAddress(rowCalculus.getRowNum(), rowCalculus.getRowNum(), 1, 6));
        CellUtil.setAlignment(cellTitle, HorizontalAlignment.LEFT);
        CellUtil.setAlignment(cellCalculus, HorizontalAlignment.LEFT);
    }

    private void createSignature(Sheet sheet, CellStyle cellStyle, int rowNum) {
        Row rowTitle = sheet.createRow(rowNum);
        Row rowDirector = sheet.createRow(rowNum + 2);
        Row rowSignature = sheet.createRow(rowNum + 4);
        Row rowMP = sheet.createRow(rowNum + 6);

        // Create cell
        Cell cellTitle = rowTitle.createCell(1);
        Cell cellDirector = rowDirector.createCell(1);
        Cell cellSignatureDirector = rowSignature.createCell(3);
        Cell cellMP = rowMP.createCell(1);

        // Set styles
        cellTitle.setCellStyle(cellStyle);
        cellDirector.setCellStyle(cellStyle);
        cellSignatureDirector.setCellStyle(cellStyle);
        cellMP.setCellStyle(cellStyle);

        // Set values
        cellTitle.setCellValue(String.format("От %s", myCompanyProperties.getMyCompanyName()));
        cellDirector.setCellValue("Директор");
        cellSignatureDirector.setCellValue(String.format("(%s)", myCompanyProperties.getLastnameWithInitial()));
        cellMP.setCellValue("М.П.");

        // Add regions
        sheet.addMergedRegion(new CellRangeAddress(rowTitle.getRowNum(), rowTitle.getRowNum(), 1, 6));
        sheet.addMergedRegion(new CellRangeAddress(rowDirector.getRowNum(), rowDirector.getRowNum(), 1, 6));
        CellRangeAddress cellRangeAddresses = new CellRangeAddress(rowSignature.getRowNum(),
                rowSignature.getRowNum(), 1, 2);
        sheet.addMergedRegion(cellRangeAddresses);
        sheet.addMergedRegion(new CellRangeAddress(rowSignature.getRowNum(), rowSignature.getRowNum(), 3, 6));

        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, cellRangeAddresses, sheet);
        CellUtil.setAlignment(cellTitle, HorizontalAlignment.LEFT);
        CellUtil.setAlignment(cellDirector, HorizontalAlignment.LEFT);
        CellUtil.setAlignment(cellSignatureDirector, HorizontalAlignment.LEFT);
    }

    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();
        CreationHelper createHelper = wb.getCreationHelper();

        // Title
        Font font = this.createFont(wb, (short)14, "Arial", true, false, false);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.put(TITLE_STYLE, cellStyle);

        // Period
        Font font1 = this.createFont(wb, (short)10, "Arial", false, false, false);
        CellStyle cellStyle1 = wb.createCellStyle();
        cellStyle1.setFont(font1);
        cellStyle1.setAlignment(HorizontalAlignment.CENTER);
        cellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle1.setWrapText(true);
        styles.put(PERIOD_STYLE, cellStyle1);

        // Agreement
        Font font2 = this.createFont(wb, (short)10, "Arial", false, false, false);
        CellStyle cellStyle2 = wb.createCellStyle();
        cellStyle2.setFont(font2);
        cellStyle2.setAlignment(HorizontalAlignment.CENTER);
        cellStyle2.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle2.setWrapText(true);
        styles.put(AGREEMENT_STYLE, cellStyle2);

        // Table title style
        Font font3 = this.createFont(wb, (short)8, "Arial", false, false, false);
        CellStyle cellStyle3 = wb.createCellStyle();
        cellStyle3.setFont(font3);
        cellStyle3.setAlignment(HorizontalAlignment.LEFT);
        cellStyle3.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle3.setWrapText(true);
        styles.put(TABLE_TITLE_STYLE, cellStyle3);

        // Table headers style
        Font font4 = this.createFont(wb, (short)8, "Arial", false, false, false);
        CellStyle cellStyle4 = wb.createCellStyle();
        cellStyle4.setFont(font4);
        cellStyle4.setAlignment(HorizontalAlignment.CENTER);
        cellStyle4.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle4.setWrapText(true);
        styles.put(TABLE_HEADER_STYLE, cellStyle4);

        // Table saldo begin style
        Font font5 = this.createFont(wb, (short)8, "Arial", true, false, false);
        CellStyle cellStyle5 = wb.createCellStyle();
        cellStyle5.setFont(font5);
        cellStyle5.setAlignment(HorizontalAlignment.CENTER);
        cellStyle5.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle5.setWrapText(true);
        styles.put(TABLE_SALDO_STYLE, cellStyle5);

        // Table operation row style
        Font font6 = this.createFont(wb, (short)8, "Arial", false, false, false);
        CellStyle cellStyle6 = wb.createCellStyle();
        cellStyle6.setFont(font6);
        cellStyle6.setAlignment(HorizontalAlignment.CENTER);
        cellStyle6.setVerticalAlignment(VerticalAlignment.CENTER);
        styles.put(TABLE_OPERATION_ROW_STYLE, cellStyle6);

        // Table operation row date style
        Font font7 = this.createFont(wb, (short)8, "Arial", false, false, false);
        CellStyle cellStyle7 = wb.createCellStyle();
        cellStyle7.setFont(font7);
        cellStyle7.setAlignment(HorizontalAlignment.CENTER);
        cellStyle7.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle7.setDataFormat(
                createHelper.createDataFormat().getFormat("dd.MM.yyyy")
        );
        styles.put(TABLE_OPERATION_DATE_ROW_STYLE, cellStyle7);

        // Calculus title
        Font font8 = this.createFont(wb, (short)8, "Arial", false, false, false);
        CellStyle cellStyle8 = wb.createCellStyle();
        cellStyle8.setFont(font8);
        cellStyle8.setAlignment(HorizontalAlignment.CENTER);
        cellStyle8.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle8.setWrapText(true);
        styles.put(CALCULUS_TITLE_STYLE, cellStyle8);

        // Calculus post title
        Font font9 = this.createFont(wb, (short)8, "Arial", true, false, false);
        CellStyle cellStyle9 = wb.createCellStyle();
        cellStyle9.setFont(font9);
        cellStyle9.setAlignment(HorizontalAlignment.CENTER);
        cellStyle9.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle9.setWrapText(true);
        styles.put(CALCULUS_POST_TITLE_STYLE, cellStyle9);

        // Calculus title
        Font font10 = this.createFont(wb, (short)8, "Arial", false, false, false);
        CellStyle cellStyle10 = wb.createCellStyle();
        cellStyle10.setFont(font10);
        cellStyle10.setAlignment(HorizontalAlignment.CENTER);
        cellStyle10.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle10.setWrapText(true);
        styles.put(SIGNATURE_STYLE, cellStyle10);

        return styles;
    }

    private Font createFont(Workbook wb,
                            short heightInPoints,
                            String fontName,
                            boolean isBold,
                            boolean isStrikeout,
                            boolean isItalic) {
        Font font = wb.createFont();
        font.setFontHeightInPoints(heightInPoints);
        font.setFontName(fontName);
        font.setBold(isBold);
        font.setStrikeout(isStrikeout);
        font.setItalic(isItalic);
        return font;
    }
}
