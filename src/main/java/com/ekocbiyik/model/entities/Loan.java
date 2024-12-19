package com.ekocbiyik.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "t_loan")
public class Loan extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Double loanAmount;
    private Double totalAmount;
    private Double interestRate;
    private Integer numberOfInstallments;
    private Boolean isPaid;

}
