package com.ekocbiyik.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanPaymentDTO {
    private Long userId;
    private Long loanId;
    private Double amount;
}