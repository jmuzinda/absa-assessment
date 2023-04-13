package com.absabanking.rest;

import com.absabanking.domain.Bank;
import com.absabanking.domain.Client;
import com.absabanking.enums.ResponseCode;
import com.absabanking.repository.AccountRepository;
import com.absabanking.repository.CardRepository;
import com.absabanking.service.BankService;
import com.absabanking.service.ClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client")
@Api(value = "clients", description = "Manage clients")
@CrossOrigin
public class ClientRestController {

    private final ClientService clientService;
    private final BankService bankService;
    private AccountRepository accountRepository;
    private CardRepository cardRepository;

    @Autowired
    public ClientRestController(ClientService clientService, BankService bankService, AccountRepository accountRepository, CardRepository cardRepository) {
        this.clientService = clientService;
        this.bankService = bankService;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
    }

    @GetMapping(value = {"", "/"})
    @ApiOperation(value = "Home for the bank rest service")
    public ResponseEntity home() {
        return new ResponseEntity(ResponseCode.B001, HttpStatus.OK);
    }

    @PostMapping("/{bankCode}/create")
    @ApiOperation(value = "Create new bank client")
    ResponseEntity createClient(@RequestBody Client client, @PathVariable String bankCode) {
        clientService.createBankClient(client, bankCode);
        return new ResponseEntity(ResponseCode.C000.getDescription(), HttpStatus.OK);
    }

    @GetMapping("/list")
    @ApiOperation(value = "View a list of available clients", response = Iterable.class)
    public Iterable list() {
        return clientService.GetAllClients();
    }

    @GetMapping(value = "/find/{id}", produces = "application/json")
    @ApiOperation(value = "Search a client with an ID", response = Bank.class)
    public Client getBranchById(@PathVariable("id") long id) {
        return clientService.findClientById(id);
    }

    @GetMapping(value = "/count")
    @ApiOperation(value = "Total number of clients")
    public long clientsCount() {
        return clientService.clientCount();
    }
}
