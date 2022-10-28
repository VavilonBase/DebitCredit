package com.vavilon.debitcredit.controllers;

import com.vavilon.debitcredit.controllers.utils.CollectorUtils;
import com.vavilon.debitcredit.entities.Company;
import com.vavilon.debitcredit.entities.CompanyAccountOperation;
import com.vavilon.debitcredit.entities.Document;
import com.vavilon.debitcredit.entities.dtos.IntervalDate;
import com.vavilon.debitcredit.services.CompanyService;
import com.vavilon.debitcredit.services.DocumentService;
import com.vavilon.debitcredit.services.ExcelService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService companyService;
    private final DocumentService documentService;
    private final ExcelService excelService;
    public CompanyController(CompanyService companyService, DocumentService documentService, ExcelService excelService) {
        this.companyService = companyService;
        this.documentService = documentService;
        this.excelService = excelService;
    }

    @GetMapping
    public String getCompanyList(
            @RequestParam(required = false) Company company,
            Model model) {
        if (company != null) model.addAttribute("company", company);
        this.setModelForCompanyList(model);
        return "companyList";
    }

    @PostMapping
    public String saveCompany(
            @Valid Company company,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = CollectorUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            this.setModelForCompanyList(model);
            model.addAttribute("company", company);
            return "companyList";
        }

        companyService.saveCompany(company);

        return "redirect:/company";
    }

    @GetMapping("/{company}")
    public String getCompanyAccountOperationList(
            @PathVariable Company company,
            @Valid IntervalDate intervalDate,
            Model model
    ) {
        this.setModelForCompanyAccountOperationList(model, company, intervalDate);
        model.addAttribute("intervalDate", intervalDate);
        return "companyAccountOperationList";
    }

    @GetMapping("/{company}/download")
    public ResponseEntity<ByteArrayResource> generateReconciliationReport(
            @PathVariable Company company,
            @Valid IntervalDate intervalDate) {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                String excelFileName = UUID.randomUUID() + "_workbook.xlsx";
                HttpHeaders header = new HttpHeaders();
                header.setContentType(new MediaType("application", "force-download"));
                header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + excelFileName);
                excelService.generateReconciliationReport(company, intervalDate, stream);

                return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()),
                        header, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

    @GetMapping("/{company}/{operation}/delete")
    public String getCompanyAccountOperationDeleteDialog(
            @PathVariable Company company,
            @PathVariable(name = "operation") CompanyAccountOperation companyAccountOperation,
            @RequestParam(name = "OK", required = false) Boolean isDelete,
            Model model
    ) {
        if (isDelete != null && isDelete) {
            companyService.deleteCompanyAccountOperation(companyAccountOperation);
            return "redirect:/company/" + company.getId();
        } else {
            model.addAttribute("company", company);
            model.addAttribute("operation", companyAccountOperation);
            return "companyAccountOperationDialog";
        }
    }

    @PostMapping("/{company}")
    public String saveCompanyAccountOperation(
            @Valid CompanyAccountOperation companyAccountOperation,
            BindingResult bindingResult,
            Model model
    ) {
        Company company = companyAccountOperation.getCompany();

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = CollectorUtils.getErrors(bindingResult);
            if (errorsMap.containsKey("documentError")) {
                errorsMap.replace("documentError", "Необходимо выбрать документ");
            }
            if (errorsMap.containsKey("documentDateError")) {
                errorsMap.replace("documentDateError", "Введите корректную дату");
            }
            if (errorsMap.containsKey("accountOperationError")) {
                errorsMap.replace("accountOperationError", "Необходимо ввести корректную сумму списания (начисления)");
            }

            model.addAttribute("operation", companyAccountOperation);
            model.mergeAttributes(errorsMap);

            this.setModelForCompanyAccountOperationList(model, company, null);

            return "companyAccountOperationList";
        }

        companyService.addNewCompanyAccountOperation(companyAccountOperation);

        return "redirect:/company/" + company.getId();
    }

    @DeleteMapping("/{company}")
    public String deleteCompanyAccountOperation(
            @PathVariable Company company,
            @RequestParam(name = "operation") CompanyAccountOperation companyAccountOperation
    ) {
        companyService.deleteCompanyAccountOperation(companyAccountOperation);
        return "redirect:/company/" + company.getId();
    }

    private void setModelForCompanyList(Model model) {
        List<Company> companyList = companyService.getCompanyList();

        model.addAttribute("companyList", companyList);
    }

    private void setModelForCompanyAccountOperationList(Model model, Company company, IntervalDate intervalDate) {
        List<CompanyAccountOperation> companyAccountOperationList = companyService.getCompanyAccountOperationList(company, intervalDate);
        List<Document> documentList = documentService.getDocumentList();

        model.addAttribute("company", company);
        model.addAttribute("documentList", documentList);
        model.addAttribute("operationSet", companyAccountOperationList);
        model.addAttribute("dateStart", intervalDate.getDateStart());
        model.addAttribute("dateEnd", intervalDate.getDateEnd());
    }
}
