package com.ekocbiyik.controller;

import com.ekocbiyik.model.dto.CustomerInfoDTO;
import com.ekocbiyik.service.CustomerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customerInfo")
public class CustomerInfoController {

    @Autowired
    private CustomerInfoService customerInfoService;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody CustomerInfoDTO customerInfoDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(customerInfoService.createCustomerInfo(customerInfoDTO));
    }

    @GetMapping("/list")
    public ResponseEntity list() {
        return ResponseEntity.status(HttpStatus.OK).body(customerInfoService.getAllCustomerInfo());
    }

}
