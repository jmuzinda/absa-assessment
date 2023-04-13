package com.absabanking.service;

import com.absabanking.domain.Account;
import com.absabanking.domain.Address;
import com.absabanking.domain.Bank;
import com.absabanking.domain.Card;
import com.absabanking.domain.Client;
import com.absabanking.domain.Contact;
import com.absabanking.enums.EPreferredContactType;
import com.absabanking.enums.ESex;
import com.absabanking.exception.ClientAlreadyExistsException;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.BankRepository;
import com.absabanking.repository.CardRepository;
import com.absabanking.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SpringBootTest
@RunWith(JUnit4.class)
class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;
    @Mock
    private BankRepository bankRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ClientRepository clientRepository;
    @Test
    public void clientWithCorrectDetailsShouldSuccessfulyCreateNewClient() {

        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();

        Mockito.when(clientRepository.existsByIdNumber(client.getIdNumber())).thenReturn(false);
        Mockito.when(clientRepository.existsByPassportNumber(client.getPassportNumber())).thenReturn(false);

        Mockito.when(clientRepository.save(client)).thenReturn(client);
        clientService.createBankClient(client, bank.getBankCode());

        Mockito.verify(clientRepository).save(client);
    }
    @Test
    public void clientWithExistingIdShouldThrowClientAlreadyExistsException() {

        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();
        Mockito.when(clientRepository.save(client)).thenReturn(client);

        clientService.createBankClient(client, bank.getBankCode());

        Client client2 = createClient();

        Mockito.when(clientRepository.existsByIdNumber(client2.getIdNumber())).thenReturn(true);

        assertThrows(ClientAlreadyExistsException.class,
                () -> clientService.createBankClient(client2, bank.getBankCode()));
    }

    @Test
    public void givenSeedForAccountNumberReturnSeedIfClientIsFirst() {
        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();


        Mockito.when(cardRepository.findTopByOrderByCardNumberDesc()).thenReturn(null);

        Mockito.when(clientRepository.save(client)).thenReturn(client);

        clientService.createBankClient(client, bank.getBankCode());

        assertEquals(new ArrayList<>(client.getAccounts()).get(0).getBankCards().get(0).getCardNumber(), 519700000000l);
    }

    @Test
    public void givenSeedForAccountNumberReturnSeedPlusOneIfClientIsNotFirst() {
        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();
        Mockito.when(cardRepository.findTopByOrderByCardNumberDesc()).thenReturn(new Card(519700000000l));

        Mockito.when(clientRepository.save(client)).thenReturn(client);

        clientService.createBankClient(client, bank.getBankCode());

        assertEquals(new ArrayList<>(client.getAccounts()).get(0).getBankCards().get(0).getCardNumber(), 519700000000l + 1);
    }

    @Test
    public void givenNewClientMustHaveTwoAccountsCreated() {
        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();


        Mockito.when(cardRepository.findTopByOrderByCardNumberDesc()).thenReturn(null);

        Mockito.when(clientRepository.save(client)).thenReturn(client);

        clientService.createBankClient(client, bank.getBankCode());

        assertEquals(2, client.getAccounts().size());
    }

    @Test
    public void givenNewClientSavingAccountMustHaveSignOnBonus() {
        Bank bank = createBank();
        Mockito.when(bankRepository.save(bank)).thenReturn(bank);
        Client client = createClient();


        Mockito.when(cardRepository.findTopByOrderByCardNumberDesc()).thenReturn(null);

        Mockito.when(clientRepository.save(client)).thenReturn(client);

        clientService.createBankClient(client, bank.getBankCode());

        // fetch saving account
        Account savingsAccount = client.getAccounts().stream()
                .filter(account -> account.getAccountType().equalsIgnoreCase("Savings")).findFirst().get();

        assertEquals(BigDecimal.valueOf(500), savingsAccount.getAccountBalance());
    }
    private Bank createBank() {
        return new Bank("ABSA", "ABSA_" + new Random().nextInt(), EPreferredContactType.SMS);
    }

    private Client createClient() {
        Client client = new Client();
        Address address = new Address("Dummy line 1", "Dummy line 2", 3647L, "Sandton");
        client.setClientAddress(address);
        Contact contact = new Contact(761494765L, "test@gmail.com",79595685L);
        client.setClientContact(contact);
        client.setDateOfBirth(LocalDate.now().minusYears(20));
        client.setDependents(4);
        client.setEducation("Masters");
        client.setESex(ESex.FEMALE);
        client.setIdNumber(759596865L);
        client.setMonthlyExpenses(150);
        client.setEPreferredContactType(EPreferredContactType.SMS);
        client.setPassportNumber("AFT8745");
        client.setRace("African");
        client.setReceiveNotification(true);
        client.setSurname("DummySurname");
        return client;
    }
}