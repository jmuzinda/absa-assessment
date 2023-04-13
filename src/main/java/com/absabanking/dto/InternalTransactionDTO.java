package com.absabanking.dto;

import com.absabanking.enums.ETranType;
import com.absabanking.domain.Transaction;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalTransactionDTO {
    @NotNull
    private Long senderAccount;
    @NotNull
    private Long receiverAccount;
    private String narrative;
    private BigDecimal transactionAmount;
    @Enumerated(EnumType.STRING)
    private ETranType eTranType;

    public static Transaction getInstance(InternalTransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(dto.getTransactionAmount());
        transaction.setSenderAccount(dto.getSenderAccount());
        transaction.setReceiverAccount(dto.getReceiverAccount());
        transaction.setReference(dto.getNarrative());
        transaction.setTranType(dto.getETranType());
        return transaction;
    }
}
