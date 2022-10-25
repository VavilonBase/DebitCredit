package com.vavilon.debitcredit.services;

import com.vavilon.debitcredit.configs.MyCompanyProperties;
import com.vavilon.debitcredit.entities.Company;
import com.vavilon.debitcredit.entities.CompanyAccountOperation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Service
public class ExcelService {
    private final String TITLE_STYLE = "title";
    private final String PERIOD_STYLE = "period";
    private final String AGREEMENT_STYLE = "agreement";

    @Value("${upload.path}")
    private String uploadPath;

    private final MyCompanyProperties myCompanyProperties;
    private final CompanyService companyService;

    public ExcelService(MyCompanyProperties myCompanyProperties, CompanyService companyService) {
        this.myCompanyProperties = myCompanyProperties;
        this.companyService = companyService;
    }

    public void generateReconciliationReport(Company company) throws IOException {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        String excelFileName = UUID.randomUUID() + "_workbook.xlsx";
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Акт сверки");
        Map<String, CellStyle> styles = this.createStyles(wb);
        List<CompanyAccountOperation> companyAccountOperationSet = companyService.getCompanyAccountOperationList(company);

        this.createTitle(sheet, styles.get(TITLE_STYLE));
        this.createDateReconciliationReport(wb, sheet, styles.get(PERIOD_STYLE), company);
        this.createAgreement(wb, sheet, styles.get(AGREEMENT_STYLE), company);
        try (OutputStream fileOut = new FileOutputStream(uploadPath + "/" + excelFileName)) {
            wb.write(fileOut);
        }
    }

    private void createTitle(Sheet sheet, CellStyle cellStyle) {
        Row row = sheet.createRow(1);
        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Акт сверки");

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 14));
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
    }

    private void createDateReconciliationReport(Workbook wb, Sheet sheet, CellStyle cellStyle, Company company) {
        Row row = sheet.createRow(2);
        Cell cell = row.createCell(1);
        cell.setCellStyle(cellStyle);
        row.setHeightInPoints(38);
        CreationHelper createHelper = wb.getCreationHelper();
        cell.setCellValue(createHelper.createRichTextString(
                String.format("""
                                взаимных расчетов за период: 01.01.2022 - 01.10.2022
                                между %s (ИНН %s)
                                и %s (ИНН %s)""",
                        myCompanyProperties.getMyCompanyName(),
                        myCompanyProperties.getMyCompanyINN(),
                        company.getName(),
                        company.getInn())
        ));

        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 14));
        CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
    }

    private void createAgreement(Workbook wb, Sheet sheet, CellStyle cellStyle, Company company) {
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

    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();

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
