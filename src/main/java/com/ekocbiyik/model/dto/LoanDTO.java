package com.ekocbiyik.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Long loanId;
    private Long userId;
    private Double amount;
    private Double totalAmount;
    private Double interestRate;
    private int installmentCount;
    private List<LoanInstallmentDTO> installments;
}