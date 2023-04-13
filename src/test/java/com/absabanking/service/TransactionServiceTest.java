package com.absabanking.service;

import com.absabanking.dto.InternalTransactionDTO;
import com.absabanking.enums.EAccountType;
import com.absabanking.enums.ETranType;
import com.absabanking.exception.SavingsAccountException;
import com.absabanking.domain.Account;
import com.absabanking.domain.Transaction;
import com.absabanking.repository.BankRepository;
import com.absabanking.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(JUnit4.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private BankRepository bankRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private Transaction createTransaction() {
        Transaction transaction = new Transaction();
        return transaction;
    }

    private Account createAccount(EAccountType accountType, BigDecimal balance, Long accountNumber) {
        Account account = new Account();
        account.setAccountBalance(balance);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType.toString());
        return account;
    }

    @Test
    public void testTransferFromCurrentToAnotherAccountChangesShouldBeMadeToBothAccounts() throws Exception {
        InternalTransactionDTO internalTransactionDto = createInternalTransactionDTO();
        Account senderAccount = createAccount(EAccountType.CURRENT, BigDecimal.valueOf(30000),
                internalTransactionDto.getSenderAccount());
        Account receiveAccount = createAccount(EAccountType.CURRENT, BigDecimal.ZERO, internalTransactionDto.getReceiverAccount());

        BigDecimal initialSenderAmount = senderAccount.getAccountBalance();
        BigDecimal initialReceiverBalance = receiveAccount.getAccountBalance();

        Mockito.when(accountService.findAccountByAccountNumber(senderAccount.getAccountNumber()))
                .thenReturn(senderAccount);
        Mockito.when(accountService.findAccountByAccountNumber(receiveAccount.getAccountNumber()))
                .thenReturn(receiveAccount);

        transactionService.postInternalTransfer(internalTransactionDto);

        Mockito.doNothing().when(accountService).updateAccount(senderAccount);
        Mockito.doNothing().when(accountService).updateAccount(receiveAccount);

        BigDecimal postTransferSenderBalance = senderAccount.getAccountBalance();
        BigDecimal postTransferReceiverBalance = receiveAccount.getAccountBalance();

        Mockito.verify(accountService).updateAccount(senderAccount);
        Mockito.verify(accountService).updateAccount(receiveAccount);

        assertNotEquals(initialSenderAmount, postTransferSenderBalance);
        assertNotEquals(initialReceiverBalance, postTransferReceiverBalance);
    }

    @Test
    public void testSenderBalanceIsLessThanTransferAmountShouldThrowException() throws Exception {

        Account senderAccount = createAccount(EAccountType.CURRENT, BigDecimal.valueOf(200), 2000L);
        Account receiveAccount = createAccount(EAccountType.CURRENT, BigDecimal.ZERO, 78000L);

        Mockito.when(accountService.findAccountByAccountNumber(senderAccount.getAccountNumber()))
                .thenReturn(senderAccount);
        Mockito.when(accountService.findAccountByAccountNumber(receiveAccount.getAccountNumber()))
                .thenReturn(receiveAccount);

        assertThrows(Exception.class,
                () -> transactionService.postInternalTransfer(createInternalTransactionDTO()));

    }

    @Test
    public void transferMoneyFromSavingsAccountShouldThrowSavingsAccountException() throws Exception {

        InternalTransactionDTO internalTransactionDto = createInternalTransactionDTO();

        Account account = createAccount(EAccountType.SAVINGS, BigDecimal.valueOf(30000), internalTransactionDto.getSenderAccount());

        Mockito.when(accountService.findAccountByAccountNumber(account.getAccountNumber()))
                .thenReturn(account);

        assertThrows(SavingsAccountException.class,
                () -> transactionService.postInternalTransfer(internalTransactionDto));
    }

    @Test
    void handleDeposit() {
    }

    @Test
    void processTransactionsForOtherBanks() {
    }

    @Test
    void postInternalTransfer() {
    }

    private InternalTransactionDTO createInternalTransactionDTO() {

        return new InternalTransactionDTO(200000L, 10000L,
                "Internal transfer", BigDecimal.valueOf(300), ETranType.INTERNAL_TRANSFER);
    }
}