package com.vavilon.debitcredit.repositories;

import com.vavilon.debitcredit.entities.CompanyAccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyAccountOperationRepo extends JpaRepository<CompanyAccountOperation, Long> {
}
