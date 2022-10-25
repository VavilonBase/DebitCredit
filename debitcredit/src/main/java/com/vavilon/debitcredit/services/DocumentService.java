package com.vavilon.debitcredit.services;

import com.vavilon.debitcredit.entities.Document;
import com.vavilon.debitcredit.repositories.DocumentRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {
    private final DocumentRepo documentRepo;

    public DocumentService(DocumentRepo documentRepo) {
        this.documentRepo = documentRepo;
    }

    public List<Document> getDocumentList() {
        return documentRepo.findAll();
    }

    public void saveDocument(Document document) {
        documentRepo.save(document);
    }
}
