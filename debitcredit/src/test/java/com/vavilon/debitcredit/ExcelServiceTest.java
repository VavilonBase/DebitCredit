package com.vavilon.debitcredit;

import com.vavilon.debitcredit.entities.Company;
import com.vavilon.debitcredit.repositories.CompanyRepo;
import com.vavilon.debitcredit.services.ExcelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(value = {"/sql/init_db_before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/init_db_after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ExcelServiceTest {
    @Autowired
    private ExcelService excelService;

    @Autowired
    private CompanyRepo companyRepo;

    @Test
    @Transactional
    public void generateReconciliationReportTest() {
        Company company = companyRepo.findById(1l).orElse(new Company());
        assertDoesNotThrow(() -> excelService.generateReconciliationReport(company));
    }
}
