package com.ekocbiyik.service;

import com.ekocbiyik.exception.AuthorizationException;
import com.ekocbiyik.exception.BadRequestException;
import com.ekocbiyik.model.dto.CustomerInfoDTO;
import com.ekocbiyik.model.entities.CustomerInfo;
import com.ekocbiyik.model.entities.Role;
import com.ekocbiyik.model.entities.User;
import com.ekocbiyik.repository.ICustomerInfoRepository;
import com.ekocbiyik.repository.IRoleRepository;
import com.ekocbiyik.repository.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(rollbackFor = Exception.class)
@Service
public class CustomerInfoService {

    private IUserRepository userRepository;
    private ICustomerInfoRepository customerRepository;
    private IRoleRepository roleRepository;

    public CustomerInfoService(IUserRepository userRepository,
                               ICustomerInfoRepository customerRepository,
                               IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
    }

    public CustomerInfoDTO createCustomerInfo(CustomerInfoDTO customerInfoDTO) {

        if (customerInfoDTO.getUserId() == null
                || customerInfoDTO.getFirstName().isEmpty()
                || customerInfoDTO.getLastName().isEmpty()
                || customerInfoDTO.getCreditLimit() == null || customerInfoDTO.getCreditLimit() <= 0
        ) {
            throw new BadRequestException("Customer parameters cannot be empty!");
        }

        User user = userRepository.findUserById(customerInfoDTO.getUserId());
        if (user == null) {
            throw new BadRequestException("User does not exists!");
        }

        Role roleCustomer = roleRepository.findByName("ROLE_CUSTOMER");
        if (!user.getRoles().contains(roleCustomer)) {
            throw new AuthorizationException("User does not have permission!");
        }

        CustomerInfo customerInfo = customerRepository.findByUserId(customerInfoDTO.getUserId());
        if (customerInfo != null) {
            throw new BadRequestException("Customer account already exists for this user!");
        }

        customerInfo = CustomerInfo.builder()
                .userId(customerInfoDTO.getUserId())
                .firstName(customerInfoDTO.getFirstName())
                .lastName(customerInfoDTO.getLastName())
                .creditLimit(customerInfoDTO.getCreditLimit())
                .usedCreditLimit(0.0)
                .build();
        customerRepository.save(customerInfo);

        customerInfoDTO.setUsedCreditLimit(customerInfo.getUsedCreditLimit());
        return customerInfoDTO;
    }

    public List<CustomerInfoDTO> getAllCustomerInfo() {
        return customerRepository.findAll()
                .stream()
                .map(c -> CustomerInfoDTO.builder()
                        .userId(c.getUserId())
                        .firstName(c.getFirstName())
                        .lastName(c.getLastName())
                        .creditLimit(c.getCreditLimit())
                        .usedCreditLimit(c.getUsedCreditLimit())
                        .availableCreditLimit(c.getAvailableCreditLimit())
                        .build())
                .collect(Collectors.toList());
    }

}
