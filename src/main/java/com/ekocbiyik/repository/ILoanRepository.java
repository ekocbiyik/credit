package com.ekocbiyik.repository;

import com.ekocbiyik.model.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ILoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByUserId(Long userId);

}
