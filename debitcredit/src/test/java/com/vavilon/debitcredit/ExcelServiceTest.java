package com.vavilon.debitcredit;

import com.vavilon.debitcredit.entities.Company;
import com.vavilon.debitcredit.entities.dtos.IntervalDate;
import com.vavilon.debitcredit.repositories.CompanyRepo;
import com.vavilon.debitcredit.services.ExcelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(value = {"/sql/init_db_before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/init_db_after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ExcelServiceTest {
    @Value("${upload.path}")
    private String uploadPath;
    @Autowired
    private ExcelService excelService;

    @Autowired
    private CompanyRepo companyRepo;

    @Test
    @Transactional
    public void generateReconciliationReportTest() {
        Company company = companyRepo.findById(5L).orElse(new Company());
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        String excelFileName = UUID.randomUUID() + "_workbook.xlsx";
        assertDoesNotThrow(() -> excelService.generateReconciliationReport(company, new IntervalDate(
                (new SimpleDateFormat("dd.MM.yyyy")).parse("03.03.2022"),
                (new SimpleDateFormat("dd.MM.yyyy")).parse("15.03.2022")
        ), new FileOutputStream(uploadPath + "/" + excelFileName)));
    }
}
