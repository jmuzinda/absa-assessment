package com.absabanking.domain;

import com.absabanking.enums.EBankCardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Card extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private long cardNumber;

    private long pin ;
    private int cvv ;
    @Enumerated(EnumType.STRING)

    private EBankCardStatus eBankCardStatus;
    @Column(name = "card_expiry")
    private LocalDateTime cardExpiry =LocalDateTime.now().plusYears(1);
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    public Card(long cardNumber) {
        this.cardNumber = cardNumber;
    }
}
