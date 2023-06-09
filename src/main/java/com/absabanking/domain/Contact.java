package com.absabanking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Contact {

    private Long cellNumber;
    private String email;
    private Long homePhone;
}