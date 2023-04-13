package com.absabanking.repository;

import com.absabanking.domain.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Bank findBankByBankCode(String bankCode);
    Boolean existsByBankCode(String bankCode);


}