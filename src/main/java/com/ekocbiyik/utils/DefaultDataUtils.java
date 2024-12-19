package com.ekocbiyik.utils;

import com.ekocbiyik.model.dto.LoanDTO;
import com.ekocbiyik.model.entities.CustomerInfo;
import com.ekocbiyik.model.entities.Role;
import com.ekocbiyik.model.entities.User;
import com.ekocbiyik.repository.ICustomerInfoRepository;
import com.ekocbiyik.repository.IRoleRepository;
import com.ekocbiyik.repository.IUserRepository;
import com.ekocbiyik.service.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

@Slf4j
@Component
public class DefaultDataUtils {

    private IRoleRepository roleRepository;
    private IUserRepository userRepository;
    private ICustomerInfoRepository customerRepository;
    private LoanService loanService;

    public DefaultDataUtils(IRoleRepository roleRepository,
                            IUserRepository userRepository,
                            ICustomerInfoRepository customerRepository,
                            LoanService loanService) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.loanService = loanService;
    }

    @PostConstruct
    public void generateCustomData() {
        try {
            createUserAndRole();
            createCustomerInfo();
            createLoan();
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private void createUserAndRole() {
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");
        Role roleCustomer = roleRepository.findByName("ROLE_CUSTOMER");

        if (roleAdmin == null) {
            roleAdmin = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
            log.info("Role Admin created!");
        }

        if (roleCustomer == null) {
            roleCustomer = roleRepository.save(Role.builder().name("ROLE_CUSTOMER").build());
            log.info("Role Customer created!");
        }

        if (userRepository.findByUsername("admin") == null) {
            User adminUser = User.builder()
                    .username("admin")
                    .password(new BCryptPasswordEncoder().encode("admin"))
                    .enabled(true)
                    .roles(Set.of(roleAdmin))
                    .build();
            userRepository.save(adminUser);
            log.info("Admin user created!");
        }

        for (int i = 1; i < 6; i++) {
            if (userRepository.findByUsername("customer" + i) == null) {
                User custUser = User.builder()
                        .username("customer" + i)
                        .password(new BCryptPasswordEncoder().encode("customer" + i))
                        .enabled(true)
                        .roles(Set.of(roleCustomer))
                        .build();
                userRepository.save(custUser);
                log.info("{} user created!", custUser.getUsername());
            }
        }
    }

    private void createCustomerInfo() {

        for (int i = 1; i < 6; i++) {

            User user = userRepository.findByUsername("customer" + i);
            CustomerInfo customerInfo = customerRepository.findByUserId(user.getId());
            if (customerInfo != null) {
                continue;
            }

            customerInfo = CustomerInfo.builder()
                    .userId(user.getId())
                    .firstName("customer" + i)
                    .lastName("lastnameCustomer" + i)
                    .creditLimit(new Random().nextInt(1_000_000) + 99_999.0)
                    .usedCreditLimit(0.0)
                    .build();

            customerRepository.save(customerInfo);
            log.info("CustomerInfo created for user: {} !", user.getUsername());
        }
    }

    private void createLoan() {

        User user = userRepository.findByUsername("customer5");
        List<LoanDTO> loanByUserId = loanService.getAllLoan().stream().filter(l -> Objects.equals(l.getUserId(), user.getId())).toList();

        if (!loanByUserId.isEmpty()) {
            return;
        }

        LoanDTO loanDTO = LoanDTO.builder()
                .userId(user.getId())
                .amount(10_000.0)
                .interestRate(0.5)
                .installmentCount(12)
                .build();

        loanService.createLoan(loanDTO);
        log.info("Loan created for user: {} !", user.getUsername());
    }

}
