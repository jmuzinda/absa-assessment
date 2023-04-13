package com.absabanking.domain;


import com.absabanking.enums.EPreferredContactType;
import com.absabanking.enums.ESex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "client")
public class Client extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "surname",  nullable = false)
    private String surname;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private ESex eSex;
    private String race;
    private String education;
    private int dependents;
    private float monthlyExpenses;
    @Column(unique = true)
    private Long idNumber;
    @Column(unique = true)
    private String passportNumber;
    private Boolean receiveNotification;
    @Enumerated(EnumType.STRING)
    private EPreferredContactType ePreferredContactType;
    @Embedded
    private Address clientAddress;
    @Embedded
    private Contact clientContact;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Account> accounts;

    /**
     * Util methods  for adding a collection
     * @param accounts accounts linked to a client
     */
    public void addAccounts(Set<Account> accounts) {
        this.accounts = accounts;
        accounts.forEach(account -> account.setClient(this));
    }
}
