package com.ekocbiyik.service;

import com.ekocbiyik.exception.AuthorizationException;
import com.ekocbiyik.exception.BadRequestException;
import com.ekocbiyik.model.dto.LoanDTO;
import com.ekocbiyik.model.dto.LoanInstallmentDTO;
import com.ekocbiyik.model.dto.LoanPaymentDTO;
import com.ekocbiyik.model.dto.LoanPaymentResultDTO;
import com.ekocbiyik.model.entities.*;
import com.ekocbiyik.repository.*;
import com.ekocbiyik.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class LoanService {

    private ICustomerInfoRepository customerInfoRepository;
    private ILoanRepository loanRepository;
    private ILoanInstallmentRepository loanInstallmentRepository;
    private IUserRepository userRepository;
    private IRoleRepository roleRepository;

    public LoanService(ICustomerInfoRepository customerInfoRepository,
                       ILoanRepository loanRepository,
                       ILoanInstallmentRepository loanInstallmentRepository,
                       IUserRepository userRepository,
                       IRoleRepository roleRepository) {
        this.customerInfoRepository = customerInfoRepository;
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public LoanDTO createLoan(LoanDTO loanDTO) {

        if (!List.of(6, 9, 12, 24).contains(loanDTO.getInstallmentCount())) {
            throw new BadRequestException("Invalid number of installments. Allowed values are 6, 9, 12, 24.");
        }

        if (loanDTO.getInterestRate() < 0.1 || loanDTO.getInterestRate() > 0.5) {
            throw new BadRequestException("Interest rate must be between 0.1 and 0.5.");
        }

        CustomerInfo customerInfo = customerInfoRepository.findByUserId(loanDTO.getUserId());
        if (customerInfo == null) {
            throw new BadRequestException("CustomerInfo not found.");
        }

        // kredi toplam tutarı, kredi limiti tarafından karşılanıyor mu?
        double totalLoanAmount = loanDTO.getAmount() * (1 + loanDTO.getInterestRate());
        if ((customerInfo.getAvailableCreditLimit()) < totalLoanAmount) {
            throw new BadRequestException("Insufficient credit limit.");
        }

        Loan loan = Loan.builder()
                .userId(customerInfo.getUserId())
                .loanAmount(loanDTO.getAmount())
                .totalAmount(totalLoanAmount)
                .interestRate(loanDTO.getInterestRate())
                .numberOfInstallments(loanDTO.getInstallmentCount())
                .isPaid(false)
                .build();
        loanRepository.save(loan);

        OffsetDateTime installmentDate = OffsetDateTime.now().withHour(17).withMinute(0).withSecond(0).withNano(0);
        double installmentAmount = totalLoanAmount / loanDTO.getInstallmentCount();

        loanDTO.setLoanId(loan.getId());
        loanDTO.setTotalAmount(totalLoanAmount);
        loanDTO.setInstallments(new ArrayList<>());

        for (int i = 0; i < loanDTO.getInstallmentCount(); i++) {

            installmentDate = installmentDate.plusMonths(1).withDayOfMonth(1);

            LoanInstallment loanInstallment = LoanInstallment.builder()
                    .loanId(loan.getId())
                    .amount(installmentAmount)
                    .paidAmount(0.0)
                    .dueDate(installmentDate)
                    .isPaid(false)
                    .installmentOrder(i + 1)
                    .build();
            loanInstallmentRepository.save(loanInstallment);

            loanDTO.getInstallments()
                    .add(LoanInstallmentDTO.builder()
                            .loanId(loan.getId())
                            .installmentOrder(loanInstallment.getInstallmentOrder())
                            .amount(loanInstallment.getAmount())
                            .paidAmount(0.0)
                            .dueDate(loanInstallment.getDueDate())
                            .isPaid(false)
                            .build()
                    );
        }

        customerInfo.setUsedCreditLimit(customerInfo.getUsedCreditLimit() + totalLoanAmount);
        customerInfoRepository.save(customerInfo);

        return loanDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public LoanPaymentResultDTO payLoan(LoanPaymentDTO paymentDTO) {

        checkPermission(paymentDTO.getUserId()); // yetkisiz kullanıcı kontrolü

        Loan loan = loanRepository.findById(paymentDTO.getLoanId())
                .orElseThrow(() -> new BadRequestException("Loan not found."));

        if (loan.getIsPaid()) {
            throw new BadRequestException("Loan is already fully paid.");
        }

        List<LoanInstallment> unpaidInstallments = loanInstallmentRepository.findAllByLoanIdAndPaid(paymentDTO.getLoanId(), false);
        if (unpaidInstallments.isEmpty()) {
            throw new BadRequestException("No unpaid installments found.");
        }

        // if the payment amount is less than the installment 'calculatedAmount'
        if (paymentDTO.getAmount() < calculatePaidAmount(unpaidInstallments.get(0))) {
            throw new BadRequestException("Amount not enough to paid installment!");
        }

        if (unpaidInstallments.get(0).getDueDate().isAfter(OffsetDateTime.now().plusMonths(3).withDayOfMonth(1))) {
            throw new BadRequestException("Cannot pay installments more than 3 months ahead!");
        }

        double remainingAmount = paymentDTO.getAmount();
        double totalSpentAmount = 0;

        for (LoanInstallment installment : unpaidInstallments) {
            if (installment.getDueDate().isAfter(OffsetDateTime.now().plusMonths(3).withDayOfMonth(1))) {
                break; // Cannot pay installments more than 3 months ahead
            }

            // remain amount enough for other installment?
            double calculatedPaidAmount = calculatePaidAmount(installment);
            if (remainingAmount >= calculatedPaidAmount) {
                remainingAmount -= calculatedPaidAmount;
                totalSpentAmount += calculatedPaidAmount;
                installment.setPaymentDate(OffsetDateTime.now());
                installment.setPaidAmount(calculatedPaidAmount);
                installment.setBonus(calculatedPaidAmount - installment.getAmount()); // bonus > 0 = faiz  || bonus < 0 = indirim
                installment.setPaid(true);
                loanInstallmentRepository.save(installment);
            } else {
                break; // Cannot partially pay an installment
            }
        }

        if (unpaidInstallments.stream().allMatch(LoanInstallment::isPaid)) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
        }

        // update customerInfo usedCreditLimit for paid installments
        CustomerInfo customerInfo = customerInfoRepository.findByUserId(paymentDTO.getUserId());
        customerInfo.setUsedCreditLimit(customerInfo.getCreditLimit() - totalSpentAmount);
        customerInfoRepository.save(customerInfo);

        return LoanPaymentResultDTO.builder()
                .userId(paymentDTO.getUserId())
                .loanId(loan.getId())
                .paidAmount(paymentDTO.getAmount())
                .remainAmount(remainingAmount)
                .totalSpentAmount(totalSpentAmount)
                .installmentList(
                        loanInstallmentRepository.findAllByLoanId(loan.getId())
                                .stream()
                                .map(installment ->
                                        LoanInstallmentDTO.builder()
                                                .loanId(loan.getId())
                                                .installmentOrder(installment.getInstallmentOrder())
                                                .amount(installment.getAmount())
                                                .paidAmount(installment.getPaidAmount())
                                                .dueDate(installment.getDueDate())
                                                .paymentDate(installment.getPaymentDate())
                                                .isPaid(installment.isPaid())
                                                .build())
                                .toList()
                )
                .build();
    }

    public List<LoanDTO> getAllLoan() {
        return loanRepository.findAll()
                .stream()
                .map(this::convertToLoanDTO)
                .toList();
    }

    public List<LoanDTO> getLoanByUserId(Long userId) {

        checkPermission(userId);

        return loanRepository.findAllByUserId(userId)
                .stream()
                .map(this::convertToLoanDTO)
                .toList();
    }

    private void checkPermission(Long userId) {
        User activeUser = userRepository.findByUsername(JwtUtil.getCurrentUsername());
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");
        if (!activeUser.getRoles().contains(roleAdmin)
                && !Objects.equals(userId, activeUser.getId())) {
            throw new AuthorizationException("User does not have permission!");
        }
    }

    private LoanDTO convertToLoanDTO(Loan loan) {
        List<LoanInstallmentDTO> installmentDTOList = loanInstallmentRepository.findAllByLoanId(loan.getId())
                .stream()
                .map(installment ->
                        LoanInstallmentDTO.builder()
                                .loanId(loan.getId())
                                .installmentOrder(installment.getInstallmentOrder())
                                .amount(installment.getAmount())
                                .paidAmount(installment.getPaidAmount())
                                .bonus(installment.getBonus())
                                .dueDate(installment.getDueDate())
                                .paymentDate(installment.getPaymentDate())
                                .isPaid(installment.isPaid())
                                .build())
                .toList();

        return LoanDTO.builder()
                .loanId(loan.getId())
                .userId(loan.getUserId())
                .amount(loan.getLoanAmount())
                .totalAmount(loan.getTotalAmount())
                .interestRate(loan.getInterestRate())
                .installmentCount(loan.getNumberOfInstallments())
                .installments(installmentDTOList)
                .build();
    }

    private double calculatePaidAmount(LoanInstallment installment) {

        OffsetDateTime now = OffsetDateTime.now();
        double baseAmount = installment.getAmount();

        if (now.isBefore(installment.getDueDate())) {
            // Early payment discount
            long daysBeforeDueDate = ChronoUnit.DAYS.between(now, installment.getDueDate());
            double discount = baseAmount * 0.001 * daysBeforeDueDate;
            return baseAmount - discount;
        } else if (now.isAfter(installment.getDueDate())) {
            // Late payment penalty
            long daysAfterDueDate = ChronoUnit.DAYS.between(installment.getDueDate(), now);
            double penalty = baseAmount * 0.001 * daysAfterDueDate;
            return baseAmount + penalty;
        } else {
            // Payment on the due date, no adjustments
            return baseAmount;
        }
    }

}
