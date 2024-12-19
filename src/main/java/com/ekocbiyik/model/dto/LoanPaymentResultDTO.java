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
public class LoanPaymentResultDTO {
    private Long userId;
    private Long loanId;
    private Double paidAmount;
    private Double remainAmount;
    private Double totalSpentAmount;
    private List<LoanInstallmentDTO> installmentList;
}