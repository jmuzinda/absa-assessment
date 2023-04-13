package com.absabanking.listener;

import com.absabanking.converter.EPreferredContactTypeConverter;
import com.absabanking.domain.Client;
import com.absabanking.domain.Transaction;
import com.absabanking.dto.ClientDTO;
import com.absabanking.enums.EPreferredContactType;
import com.absabanking.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class TransactionEventListener {
    public TransactionEventListener() {
    }
    private ClientRepository clientRepository;
    @Autowired
    public TransactionEventListener(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionEventListener.class);
    @EventListener(Transaction.class)
    void handleTransactionEvent(Transaction transactionEvent) {
        logger.info("_________________START of internal transfer log___________________________");
        ClientDTO recipient = getClientCommunicationDetails(transactionEvent.getSenderAccount());
        if (recipient.getPrefferedCommunicationMethod().equalsIgnoreCase(EPreferredContactType.SMS.toString())) {
            logger.info(" Sending SMS  : {} Dear " + EPreferredContactTypeConverter.GenderConverter.genderConverter(recipient.getSex()) + " , a transaction of : {} has been send to your account :{}", recipient.getCellNumber(), recipient.getClientSurname(), transactionEvent.getTransactionAmount(), LocalDateTime.now());
        } else
            logger.info(" Sending EMAIL : {} Dear " + EPreferredContactTypeConverter.GenderConverter.genderConverter(recipient.getSex()) + " , a transaction of : {} has been send to your account :{}", recipient.getEmail(), recipient.getClientSurname(), transactionEvent.getTransactionAmount(), LocalDateTime.now());
        logger.info("_________________END of internal transfer log___________________________");
    }
    /**
     * Get communication details of a bank client by passing in their account number
     *
     * @param accountNumber bank client account number
     * @return a bank client communication object
     */
    public ClientDTO getClientCommunicationDetails(long accountNumber) {
        Client bankClient = clientRepository.findClientByAccountNumber(accountNumber);
        ClientDTO client = new ClientDTO();
        client.setClientName(bankClient.getName());
        client.setClientSurname(bankClient.getSurname());
        client.setCellNumber(bankClient.getClientContact().getCellNumber());
        client.setEmail(bankClient.getClientContact().getEmail());
        client.setPrefferedCommunicationMethod(bankClient.getEPreferredContactType().name());
        client.setSex(bankClient.getESex().toString());
        return client;
    }
}