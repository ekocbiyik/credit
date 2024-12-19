package com.ekocbiyik.repository;

import com.ekocbiyik.model.entities.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ILoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    @Query("from LoanInstallment l where l.loanId = :loanId order by l.installmentOrder asc")
    List<LoanInstallment> findAllByLoanId(Long loanId);

    @Query("from LoanInstallment l where l.loanId = :loanId and l.isPaid = :paid order by l.installmentOrder asc")
    List<LoanInstallment> findAllByLoanIdAndPaid(Long loanId, boolean paid);

}
