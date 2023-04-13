package com.absabanking.repository;

import com.absabanking.domain.TransactionState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionStateRepository extends JpaRepository<TransactionState, Long> {
    TransactionState findTransactionStateById(long transactionStateId);
}
