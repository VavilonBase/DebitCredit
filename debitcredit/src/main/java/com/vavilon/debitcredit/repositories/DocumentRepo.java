package com.vavilon.debitcredit.repositories;

import com.vavilon.debitcredit.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepo extends JpaRepository<Document, Integer> {
}
