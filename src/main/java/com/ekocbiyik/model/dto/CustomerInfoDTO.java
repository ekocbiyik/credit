package com.ekocbiyik.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfoDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private Double creditLimit;
    private Double usedCreditLimit;
    private Double availableCreditLimit;
}
