package com.ekocbiyik.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "t_loan_installment")
public class LoanInstallment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long loanId;
    private Double amount;
    private Double paidAmount;
    private Double bonus; // faiz ya da indirim
    private OffsetDateTime dueDate;
    private OffsetDateTime paymentDate;
    private boolean isPaid;
    private int installmentOrder;

}
