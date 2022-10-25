package com.vavilon.debitcredit.controllers;

import com.vavilon.debitcredit.controllers.utils.CollectorUtils;
import com.vavilon.debitcredit.entities.Company;
import com.vavilon.debitcredit.entities.CompanyAccountOperation;
import com.vavilon.debitcredit.entities.Document;
import com.vavilon.debitcredit.services.CompanyService;
import com.vavilon.debitcredit.services.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService companyService;
    private final DocumentService documentService;

    public CompanyController(CompanyService companyService, DocumentService documentService) {
        this.companyService = companyService;
        this.documentService = documentService;
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
            Model model
    ) {
        this.setModelForCompanyAccountOperationList(model, company);
        return "companyAccountOperationList";
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

            model.addAttribute("operation", companyAccountOperation);
            model.mergeAttributes(errorsMap);

            this.setModelForCompanyAccountOperationList(model, company);

            return "companyAccountOperationList";
        }

        companyService.addNewCompanyAccountOperation(companyAccountOperation);

        return "redirect:/company/" + company.getId();
    }

    private void setModelForCompanyList(Model model) {
        List<Company> companyList = companyService.getCompanyList();

        model.addAttribute("companyList", companyList);
    }

    private void setModelForCompanyAccountOperationList(Model model, Company company) {
        List<CompanyAccountOperation> companyAccountOperationList = companyService.getCompanyAccountOperationList(company);
        List<Document> documentList = documentService.getDocumentList();

        model.addAttribute("company", company);
        model.addAttribute("documentList", documentList);
        model.addAttribute("operationSet", companyAccountOperationList);
    }
}
