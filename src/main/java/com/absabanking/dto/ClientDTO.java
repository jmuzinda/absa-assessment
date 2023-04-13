package com.absabanking.dto;

import lombok.Data;

@Data
public class ClientDTO {
    private String clientName;
    private String ClientSurname ;
    private Long cellNumber ;
    private String email;
    private String prefferedCommunicationMethod ;
    private String   sex ;

    @Override
    public String toString() {
        return "ClientDto{" +
                "clientName='" + clientName + '\'' +
                ", ClientSurname='" + ClientSurname + '\'' +
                ", cellNumber=" + cellNumber +
                ", email='" + email + '\'' +
                ", prefferedCommunicationMethod='" + prefferedCommunicationMethod + '\'' +
                '}';
    }
}
