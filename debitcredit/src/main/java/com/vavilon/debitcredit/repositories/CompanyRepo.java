package com.vavilon.debitcredit.repositories;

import com.vavilon.debitcredit.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepo extends JpaRepository<Company, Long> {
}
