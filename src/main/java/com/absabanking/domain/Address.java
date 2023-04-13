package com.absabanking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Address {
    private String addressLine1;
    private String addressLine2;
    private Long postalCode;
    private String city;
}
