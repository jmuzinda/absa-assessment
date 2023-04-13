package com.absabanking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "account")
public class Account extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private Long accountNumber;
    private BigDecimal accountLimit;
    private BigDecimal accountBalance;
    private String accountType;
    private LocalDate servicingDate;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id",referencedColumnName = "id", nullable = false)
    private Client client;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Card> bankCards;
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Restriction> restrictions;

    public Account(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void addCards(List<Card> bankCards) {
        this.bankCards = bankCards;
        bankCards.forEach(card -> card.setAccount(this));
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Account account = (Account) o;
        return accountNumber.equals(account.accountNumber);
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accountNumber);
    }
}
