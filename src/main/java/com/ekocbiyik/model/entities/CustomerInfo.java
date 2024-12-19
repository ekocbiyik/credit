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
@Table(name = "t_customer_info")
public class CustomerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;
    private String firstName;
    private String lastName;
    private Double creditLimit;
    private Double usedCreditLimit;

    @Transient
    private Double availableCreditLimit;

    public Double getAvailableCreditLimit() {
        return (creditLimit != null && usedCreditLimit != null) ? creditLimit - usedCreditLimit : 0.0;
    }

}
