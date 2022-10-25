package com.vavilon.debitcredit.services;

import com.vavilon.debitcredit.entities.Company;
import com.vavilon.debitcredit.entities.CompanyAccountOperation;
import com.vavilon.debitcredit.repositories.CompanyAccountOperationRepo;
import com.vavilon.debitcredit.repositories.CompanyRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CompanyService {
    private final CompanyRepo companyRepo;
    private final CompanyAccountOperationRepo operationRepo;

    public CompanyService(CompanyRepo companyRepo, CompanyAccountOperationRepo operationRepo) {
        this.companyRepo = companyRepo;
        this.operationRepo = operationRepo;
    }

    public List<Company> getCompanyList() {
        return companyRepo.findAll().stream().sorted().toList();
    }

    public List<CompanyAccountOperation> getCompanyAccountOperationList(Company company) {
        return company.getCompanyAccountOperationSet().stream().sorted().toList();
    }

    public void saveCompany(Company company) {
        companyRepo.save(company);
    }

    public void addNewCompanyAccountOperation(CompanyAccountOperation companyAccountOperation) {
        this.setCurrentBalance(companyAccountOperation);

        operationRepo.save(companyAccountOperation);
    }

    private void setCurrentBalance(CompanyAccountOperation companyAccountOperation) {
        List<CompanyAccountOperation> companyAccountOperationList =
                this.getCompanyAccountOperationList(companyAccountOperation.getCompany());

        companyAccountOperation.setAccountOperation(
                companyAccountOperation
                        .getAccountOperation()
                        .setScale(2, RoundingMode.DOWN)
        );

        BigDecimal validAccountOperation = this.getValidAccountOperation(companyAccountOperation);

        if (companyAccountOperationList.size() == 0) {
            companyAccountOperation.setCurrentBalance(validAccountOperation);
        } else {
            BigDecimal currentCompanyBalance =
                    companyAccountOperationList
                            .get(0)
                            .getCurrentBalance();

            companyAccountOperation.setCurrentBalance(
                    validAccountOperation
                            .add(currentCompanyBalance)
                            .setScale(2, RoundingMode.DOWN)
            );
        }
    }

    private BigDecimal getValidAccountOperation(CompanyAccountOperation companyAccountOperation) {
        if (companyAccountOperation.getIsDebit()) {
             return companyAccountOperation
                     .getAccountOperation();
        } else {
            return companyAccountOperation
                    .getAccountOperation()
                    .negate();
        }
    }
}
