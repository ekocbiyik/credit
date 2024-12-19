package com.ekocbiyik.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallmentDTO {
    private Long loanId;
    private int installmentOrder;
    private Double amount;
    private Double paidAmount;
    private Double bonus;
    private OffsetDateTime dueDate;
    private OffsetDateTime paymentDate;
    private boolean isPaid;
}