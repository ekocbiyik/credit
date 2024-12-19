package com.ekocbiyik.controller;

import com.ekocbiyik.model.dto.LoanDTO;
import com.ekocbiyik.model.dto.LoanPaymentDTO;
import com.ekocbiyik.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody LoanDTO loanDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(loanService.createLoan(loanDTO));
    }

    @GetMapping("/list")
    public ResponseEntity list() {
        return ResponseEntity.status(HttpStatus.OK).body(loanService.getAllLoan());
    }

    @GetMapping("/getByUserId")
    public ResponseEntity getByUserId(@RequestParam Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(loanService.getLoanByUserId(userId));
    }

    @PostMapping("/payment")
    public ResponseEntity payment(@RequestBody LoanPaymentDTO paymentDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(loanService.payLoan(paymentDTO));
    }


}
