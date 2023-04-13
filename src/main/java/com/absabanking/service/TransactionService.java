package com.absabanking.service;

import com.absabanking.dto.DepositDto;
import com.absabanking.dto.InterBankTransactionsDTo;
import com.absabanking.dto.InternalTransactionDto;
import com.absabanking.enums.EAccountType;
import com.absabanking.enums.EPostingType;
import com.absabanking.enums.ETranType;
import com.absabanking.exception.InterbankTransactionException;
import com.absabanking.exception.SavingsAccountException;
import com.absabanking.model.Account;
import com.absabanking.model.Bank;
import com.absabanking.model.Transaction;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
public class TransactionService {
    private static final org.slf4j.Logger transactionServiceLogger = org.slf4j.LoggerFactory.getLogger(TransactionService.class);
    private static final BigDecimal bankTransactionCharges = BigDecimal.valueOf(0.0005);
    private static final BigDecimal bankInterest = BigDecimal.valueOf(0.005);
    private ApplicationEventPublisher applicationEventPublisher;

    private TransactionRepository transactionRepository;
    private AccountService accountService;
    private BankService bankService;


    @Autowired
    public TransactionService(ApplicationEventPublisher applicationEventPublisher, TransactionRepository transactionRepository, AccountRepository accountRepository, AccountService accountService, BankService bankService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.bankService = bankService;
    }

    public List<Transaction> getAllBanks() {
        return transactionRepository.findAll();
    }

    public List<Transaction> findAllTransactionsByBankCode(String bankCode, ETranType eTranType) {
        return transactionRepository.findTransactionByAcquiringInstitutionAndTranType(bankCode, eTranType);
    }

    /**
     * @param depositDto
     * @param senderAccount
     */
    public void handleDeposit(DepositDto depositDto, Account senderAccount) {
        if (senderAccount != null) {
            Transaction transaction = new Transaction();
            transaction.setTranType(ETranType.DEPOSIT);
            transaction.setNarrative(String.format("Depositing money R%d into account %d", depositDto.getAmount(), depositDto.getAccountNumber()));
            transactionRepository.save(transaction);
            senderAccount.setAccountBalance(senderAccount.getAccountBalance().add(depositDto.getAmount()));
            accountService.updateAccount(senderAccount);
        }
    }

    /**
     * @param listOfInterBankTransactionsDTo
     */
    @Transactional
    public void processTransactionsForOtherBanks(List<InterBankTransactionsDTo> listOfInterBankTransactionsDTo) {
        if (!listOfInterBankTransactionsDTo.isEmpty()) {
            for (InterBankTransactionsDTo interbankTransactions : listOfInterBankTransactionsDTo) {
                Bank bankActingOnBehalf = bankService.findBankByBankCode(interbankTransactions.getActingOnBehalfBankCode());
                Bank accountHolderBank = bankService.findBankByBankCode(interbankTransactions.getAccountHolderBankCode());
                if (bankActingOnBehalf.equals(accountHolderBank)) {
                    transactionServiceLogger.error("Transaction not permitted ");
                    throw new InterbankTransactionException("Can not  process transactions , banks are the same ");
                }
                //check if the  bank has that account linked  to it
                Account account = accountService.findAccountByAccountNumberAndBankId(interbankTransactions.getAccountNumber(),accountHolderBank.getId());
                if (account != null) {
                    EPostingType postingType = interbankTransactions.getEPostingType();
                    interbankTransactions.setETranType(ETranType.ACT_ON_BEHALF);
                    switch (postingType) {
                        case CREDIT:
                            transactionServiceLogger.info("Posting a CREDIT transaction for account number : {} , with an amount of : {}", interbankTransactions.getAccountNumber(), interbankTransactions.getTransactionAmount());
                            // code block
                            account.setAccountBalance(account.getAccountBalance().add(interbankTransactions.getTransactionAmount()));
                            accountService.updateAccount(account);
                            postBankActingOnBehalfOfTransactions(listOfInterBankTransactionsDTo);
                            break;
                        case DEBIT:
                            transactionServiceLogger.info("Posting a DEBIT transaction for account number : {} , with an amount of : {}", interbankTransactions.getAccountNumber(), interbankTransactions.getTransactionAmount());
                            // code block
                            account.setAccountBalance(account.getAccountBalance().subtract(interbankTransactions.getTransactionAmount()));
                            accountService.updateAccount(account);
                            postBankActingOnBehalfOfTransactions(listOfInterBankTransactionsDTo);
                            break;
                        default:
                            transactionServiceLogger.warn("______________________NO TRANSACTIONS TO PROCESS___________________");
                    }
                }
            }
        }
    }

    /**
     * @param internalTransactionDto
     * @throws Exception
     */
    public void postInternalTransfer(InternalTransactionDto internalTransactionDto) throws Exception {
        internalTransactionDto.setETranType(ETranType.INTERNAL_TRANSFER);
        Account senderAccount = accountService.findAccountByAccountNumber(internalTransactionDto.getSenderAccount());
        if (senderAccount.getAccountType().equalsIgnoreCase(EAccountType.SAVINGS.toString())) {
            transactionServiceLogger.error("Savings account cant transfer money", internalTransactionDto.getSenderAccount());
            throw new SavingsAccountException("Savings account cant transfer money");
        }
        Account receiverAccount = accountService.findAccountByAccountNumber(internalTransactionDto.getReceiverAccount());
        if (receiverAccount != null && senderAccount != null) {
            if (senderAccount.getAccountBalance().compareTo(internalTransactionDto.getTransactionAmount()) == -1) {
                transactionServiceLogger.info("amount  being transferred is less than your current balance please  review/recharge: {}", senderAccount.getAccountNumber());
                throw new Exception("amount  being transferred is less than your current balance please  review ");
            }
            BigDecimal transactionChargesAmount = internalTransactionDto.getTransactionAmount().multiply(bankTransactionCharges);
            BigDecimal newAccountBalance = senderAccount.getAccountBalance().subtract(transactionChargesAmount.add(internalTransactionDto.getTransactionAmount()));
            senderAccount.setAccountBalance(newAccountBalance);

            if (receiverAccount.getAccountType().equalsIgnoreCase(EAccountType.SAVINGS.toString())) {
                BigDecimal savingsAccountNewAccountBalance = receiverAccount.getAccountBalance().add(receiverAccount.getAccountBalance().multiply(bankInterest)).add(internalTransactionDto.getTransactionAmount());
                receiverAccount.setAccountBalance(savingsAccountNewAccountBalance);
                accountService.updateAccount(receiverAccount);
            } else {
                BigDecimal newCurrentAccountSender = receiverAccount.getAccountBalance().add(receiverAccount.getAccountBalance()).add(internalTransactionDto.getTransactionAmount());
                receiverAccount.setAccountBalance(newCurrentAccountSender);
                accountService.updateAccount(receiverAccount);
            }
            accountService.updateAccount(senderAccount);
            postInternalTransactions(InternalTransactionDto.getInstance(internalTransactionDto));
        }

    }

    /**
     * @param listOfInterBankTransactionsDTo one or more transactions to process
     */
    private void postBankActingOnBehalfOfTransactions(List<InterBankTransactionsDTo> listOfInterBankTransactionsDTo) {
        if (!listOfInterBankTransactionsDTo.isEmpty()) {
            for (InterBankTransactionsDTo interBankTransactionsDTo : listOfInterBankTransactionsDTo) {
                transactionServiceLogger.info("posting transactions on behalf of bank started successfully", interBankTransactionsDTo.getActingOnBehalfBankCode());
                Transaction transaction = new Transaction();
                transaction.setTranType(interBankTransactionsDTo.getETranType());
                transaction.setNarrative(interBankTransactionsDTo.getNarrative());
                transaction.setTransactionAmount(interBankTransactionsDTo.getTransactionAmount());
                transaction.setTranType(ETranType.ACT_ON_BEHALF);
                transaction.setAcquiringInstitution(interBankTransactionsDTo.getActingOnBehalfBankCode());
                transaction.setEPostingType(interBankTransactionsDTo.getEPostingType());
                transactionRepository.save(transaction);
                transactionServiceLogger.info("posting transactions on behalf of bank completed successfully", interBankTransactionsDTo.getActingOnBehalfBankCode());

            }

        }
    }

    /**
     * Used to post internal transactions
     * @param transaction  the  transaction  object
     */
    private void postInternalTransactions(Transaction transaction) {
        transactionServiceLogger.info("Creating a payment of : {} , from account : {} , to account :{}", transaction.getTransactionAmount(), transaction.getSenderAccount(), transaction.getReceiverAccount());
        transaction.setTransactionCharges(transaction.getTransactionAmount().multiply(bankTransactionCharges));
        transactionRepository.save(transaction);
        applicationEventPublisher.publishEvent(transaction);  //publish event
        transactionServiceLogger.info("==========================================================================================================");
        transactionServiceLogger.info("---------------transaction executed successfully------------------");
    }
}
