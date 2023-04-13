package com.absabanking.service;

import com.absabanking.domain.Account;
import com.absabanking.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService {
    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account findAccountByAccountNumberAndAccountTypeIsCurrentAccount(Long accountNumber) {
        return accountRepository.findAccountByAccountNumber(accountNumber);
    }

    public Account findAccountByAccountNumber(Long accountNumber) {
        return accountRepository.findAccountByAccountNumber(accountNumber);
    }

    public void updateAccount(Account account) {
        accountRepository.save(account);
    }

    public Account findAccountByAccountNumberAndBankId(Long accountNumber, Long bankId) {
        return accountRepository.findAccountByAccountNumberAndBankId(accountNumber, bankId);
    }
}
