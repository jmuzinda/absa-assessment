package com.absabanking.repository;

import com.absabanking.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Boolean existsByIdNumber(Long idNumber);
    Boolean existsByPassportNumber(String idNumber);
    @Query("select c,a from Client c join Account a on c.id = a.client.id where a.accountNumber =?1")
    Client findClientByAccountNumber(Long accountNumber);

}
