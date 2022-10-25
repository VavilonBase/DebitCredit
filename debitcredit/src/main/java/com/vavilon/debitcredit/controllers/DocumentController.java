package com.vavilon.debitcredit.controllers;

import com.vavilon.debitcredit.controllers.utils.CollectorUtils;
import com.vavilon.debitcredit.entities.Document;
import com.vavilon.debitcredit.services.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/document")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public String getDocumentList(
            @RequestParam(required = false) Document document,
            Model model) {
        if (document != null) model.addAttribute(document);
        this.setModelForDocumentList(model);

        return "documentList";
    }

    @PostMapping
    public String saveDocument(
            @Valid Document document,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            this.setModelForDocumentList(model);
            Map<String, String> errorsMap = CollectorUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("document", document);
            return "documentList";
        }

        documentService.saveDocument(document);

        return "redirect:/document";
    }

    private void setModelForDocumentList(Model model) {
        List<Document> documentList = documentService.getDocumentList();

        model.addAttribute("documentList", documentList);
    }
}
