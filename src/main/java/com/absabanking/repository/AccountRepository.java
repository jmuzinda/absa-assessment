package com.absabanking.repository;

import com.absabanking.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findAccountByAccountNumber(Long accountNumber);
    Account findTopByOrderByAccountNumberDesc();
    Account findAccountByAccountNumberAndBankId(Long accountNumber, Long bankId);


}
