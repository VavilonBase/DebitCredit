package com.vavilon.debitcredit.services.utils;

import com.vavilon.debitcredit.entities.CompanyAccountOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class BeforeAndAfterCompanyAccountOperation {
    private List<CompanyAccountOperation> beforeCompanyAccountOperationList = new ArrayList<>();
    private List<CompanyAccountOperation> afterCompanyAccountOperationList = new ArrayList<>();

    public BeforeAndAfterCompanyAccountOperation(Date date, List<CompanyAccountOperation> companyAccountOperationList) {
        this.getBeforeAndAfterCompanyAccountOperationListByDate(date, companyAccountOperationList);
    }

    public BeforeAndAfterCompanyAccountOperation(Long id, List<CompanyAccountOperation> companyAccountOperationList) {
        this.getBeforeAndAfterCompanyAccountOperationListById(id, companyAccountOperationList);
    }

    public boolean isBeforeOperationsExists() {
        return beforeCompanyAccountOperationList.size() > 0;
    }

    public boolean isAfterOperationsExists() {
        return afterCompanyAccountOperationList.size() > 0;
    }

    public CompanyAccountOperation getNearBeforeOperation() {
        return this.beforeCompanyAccountOperationList.get(0);
    }

    private void getBeforeAndAfterCompanyAccountOperationListByDate(
            Date date, List<CompanyAccountOperation> companyAccountOperationList
    ) {
        for (var companyAccountOperation : companyAccountOperationList) {
            if (companyAccountOperation.getDocumentDate().equals(date)
                    || companyAccountOperation.getDocumentDate().before(date)) {
                beforeCompanyAccountOperationList.add(companyAccountOperation);
            } else {
                afterCompanyAccountOperationList.add(companyAccountOperation);
            }
        }
    }

    private void getBeforeAndAfterCompanyAccountOperationListById(Long id,
                                                                  List<CompanyAccountOperation> companyAccountOperationListSortedDec) {
        boolean findOperation = false;
        for (var companyAccountOperation : companyAccountOperationListSortedDec) {
            if (companyAccountOperation.getId().equals(id)) {
                findOperation = true;
            } else {
                if (findOperation) {
                    beforeCompanyAccountOperationList.add(companyAccountOperation);
                } else {
                    afterCompanyAccountOperationList.add(companyAccountOperation);
                }
            }
        }
    }
}
