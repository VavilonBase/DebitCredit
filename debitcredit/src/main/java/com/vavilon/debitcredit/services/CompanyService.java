package com.vavilon.debitcredit.services;

import com.vavilon.debitcredit.entities.Company;
import com.vavilon.debitcredit.entities.CompanyAccountOperation;
import com.vavilon.debitcredit.entities.dtos.IntervalDate;
import com.vavilon.debitcredit.repositories.CompanyAccountOperationRepo;
import com.vavilon.debitcredit.repositories.CompanyRepo;
import com.vavilon.debitcredit.services.utils.BeforeAndAfterCompanyAccountOperation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
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

    public List<CompanyAccountOperation> getCompanyAccountOperationList(Company company, IntervalDate intervalDate) {
        if (intervalDate != null) {
            List<CompanyAccountOperation> companyAccountOperationList;
            Date dateStart = intervalDate.getDateStart();
            Date dateEnd = intervalDate.getDateEnd();

            if (dateStart != null) {
                if (dateEnd != null) {
                    companyAccountOperationList = company.getCompanyAccountOperationSet()
                            .stream()
                            .filter(operation ->
                                    operation.getDocumentDate().compareTo(intervalDate.getDateStart()) >= 0 &&
                                            operation.getDocumentDate().compareTo(intervalDate.getDateEnd()) <= 0)
                            .sorted()
                            .toList();
                } else {
                    companyAccountOperationList = company.getCompanyAccountOperationSet()
                            .stream()
                            .filter(operation -> operation.getDocumentDate().compareTo(intervalDate.getDateStart()) >= 0)
                            .sorted()
                            .toList();
                    if (companyAccountOperationList.size() != 0) {
                        dateEnd = companyAccountOperationList.get(0).getDocumentDate();
                    }
                }
            } else {
                if (dateEnd != null) {
                    companyAccountOperationList = company.getCompanyAccountOperationSet()
                            .stream()
                            .filter(operation -> operation.getDocumentDate().compareTo(intervalDate.getDateEnd()) <= 0)
                            .sorted()
                            .toList();
                    if (companyAccountOperationList.size() != 0) {
                        dateStart = companyAccountOperationList.get(companyAccountOperationList.size() - 1).getDocumentDate();
                    }
                } else {
                    companyAccountOperationList = company.getCompanyAccountOperationSet().stream().sorted().toList();
                    if (companyAccountOperationList.size() != 0) {
                        dateStart = companyAccountOperationList.get(companyAccountOperationList.size() - 1).getDocumentDate();
                        dateEnd = companyAccountOperationList.get(0).getDocumentDate();
                    }

                }
            }
            intervalDate.setDateStart(dateStart);
            intervalDate.setDateEnd(dateEnd);
            return companyAccountOperationList;
        }
        return company.getCompanyAccountOperationSet().stream().sorted().toList();
    }

    public void saveCompany(Company company) {
        companyRepo.save(company);
    }

    public void addNewCompanyAccountOperation(CompanyAccountOperation companyAccountOperation) {
        // Get all company operation list
        List<CompanyAccountOperation> companyAccountOperationList =
                this.getCompanyAccountOperationList(companyAccountOperation.getCompany(), null);

        // Divide company account operation to after and before by add date document
        BeforeAndAfterCompanyAccountOperation
                beforeAndAfterCompanyAccountOperationList =
                new BeforeAndAfterCompanyAccountOperation(
                        companyAccountOperation.getDocumentDate(),
                        companyAccountOperationList
                );

        // Get valid account operation (positive or negative) and set scale 2 and round down
        BigDecimal validAccountOperation = this.getValidAccountOperation(companyAccountOperation);

        // Set current balance to add operation
        this.setCurrentBalance(
                beforeAndAfterCompanyAccountOperationList,
                companyAccountOperation,
                validAccountOperation
        );

        // Refresh all after company account operation by adding valid account operation to them current balance
        this.refreshAfterCompanyAccountOperation(
                beforeAndAfterCompanyAccountOperationList.getAfterCompanyAccountOperationList(),
                validAccountOperation);

        // Create list to save in DB
        List<CompanyAccountOperation> companyAccountOperationListToSave =
                new ArrayList<>(beforeAndAfterCompanyAccountOperationList.getAfterCompanyAccountOperationList());

        companyAccountOperationListToSave.add(companyAccountOperation);

        // Save list in DB
        operationRepo.saveAll(companyAccountOperationListToSave);
    }

    public void deleteCompanyAccountOperation(CompanyAccountOperation companyAccountOperation) {
        // Get all company operation list
        List<CompanyAccountOperation> companyAccountOperationList =
                this.getCompanyAccountOperationList(companyAccountOperation.getCompany(), null);

        // Divide company account operation to after and before by delete id operation
        BeforeAndAfterCompanyAccountOperation
                beforeAndAfterCompanyAccountOperationList =
                new BeforeAndAfterCompanyAccountOperation(
                        companyAccountOperation.getId(),
                        companyAccountOperationList
                );

        // Get valid account operation (positive or negative) and set scale 2 and round down
        // And reverse, because need delete operation
        BigDecimal reverseValidAccountOperation = this
                        .getValidAccountOperation(companyAccountOperation)
                        .negate();

        // Refresh all after company account operation by deleting valid account operation to them current balance
        this.refreshAfterCompanyAccountOperation(
                beforeAndAfterCompanyAccountOperationList.getAfterCompanyAccountOperationList(),
                reverseValidAccountOperation);

        // Delete operation from DB
        operationRepo.delete(companyAccountOperation);

        // Save list in DB
        operationRepo.saveAll(beforeAndAfterCompanyAccountOperationList.getAfterCompanyAccountOperationList());
    }

    private void refreshAfterCompanyAccountOperation(
            List<CompanyAccountOperation> afterCompanyAccountOperationList,
            BigDecimal validAccountOperation
    ) {
        for (var afterCompanyAccountOperation : afterCompanyAccountOperationList) {
            afterCompanyAccountOperation.setCurrentBalance(
                    afterCompanyAccountOperation
                            .getCurrentBalance()
                            .add(validAccountOperation)
                            .setScale(2, RoundingMode.DOWN)
            );
        }
    }


    private void setCurrentBalance(
            BeforeAndAfterCompanyAccountOperation beforeAndAfterCompanyAccountOperationList,
            CompanyAccountOperation companyAccountOperation,
            BigDecimal validAccountOperation
    ) {
        if (beforeAndAfterCompanyAccountOperationList.isBeforeOperationsExists()) {
            CompanyAccountOperation nearBeforeCompanyAccountOperation =
                    beforeAndAfterCompanyAccountOperationList.getNearBeforeOperation();

            BigDecimal nearBeforeCompanyAccountOperationBalance =
                    nearBeforeCompanyAccountOperation
                            .getCurrentBalance();

            companyAccountOperation.setCurrentBalance(
                    validAccountOperation
                            .add(nearBeforeCompanyAccountOperationBalance)
                            .setScale(2, RoundingMode.DOWN)
            );

        } else {
            companyAccountOperation.setCurrentBalance(validAccountOperation);
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
