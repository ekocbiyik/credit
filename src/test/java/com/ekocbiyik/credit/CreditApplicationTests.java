package com.ekocbiyik.credit;

import com.ekocbiyik.model.entities.CustomerInfo;
import com.ekocbiyik.model.entities.Role;
import com.ekocbiyik.model.entities.User;
import com.ekocbiyik.repository.ICustomerInfoRepository;
import com.ekocbiyik.repository.IRoleRepository;
import com.ekocbiyik.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class CreditApplicationTests {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private ICustomerInfoRepository customerRepository;

    @Test
    void createRoleTest() {

        Role roleAdmin = Role.builder().name("ROLE_ADMIN").build();
        Role roleCustomer = Role.builder().name("ROLE_CUSTOMER").build();

        Role savedRoleAdmin = roleRepository.save(roleAdmin);
        Role savedRoleCustomer = roleRepository.save(roleCustomer);

        assertThat(savedRoleAdmin).isNotNull();
        assertThat(savedRoleAdmin.getId()).isNotNull();
        assertThat(savedRoleAdmin.getName()).isEqualTo("ROLE_ADMIN");

        assertThat(savedRoleCustomer).isNotNull();
        assertThat(savedRoleCustomer.getId()).isNotNull();
        assertThat(savedRoleCustomer.getName()).isEqualTo("ROLE_CUSTOMER");

    }

    @Test
    void createUsersTest() {

        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");
        Role roleCustomer = roleRepository.findByName("ROLE_CUSTOMER");

        assertThat(roleAdmin).isNotNull();
        assertThat(roleCustomer).isNotNull();

        String adminName = String.format("admin%s", new Random().nextInt(99) + 1);
        String customerName = String.format("customer%s", new Random().nextInt(99) + 1);

        User adminUser = User.builder()
                .username(adminName)
                .password(new BCryptPasswordEncoder().encode(adminName))
                .enabled(true)
                .roles(Set.of(roleAdmin))
                .build();
        userRepository.save(adminUser);

        User customer = User.builder()
                .username(customerName)
                .password(new BCryptPasswordEncoder().encode(customerName))
                .enabled(true)
                .roles(Set.of(roleCustomer))
                .build();
        userRepository.save(customer);

        User savedAdmin = userRepository.findByUsername(adminName);
        User savedCust = userRepository.findByUsername(customerName);

        assertThat(savedAdmin).isNotNull();
        assertThat(savedCust).isNotNull();

        assertThat(savedAdmin.getRoles().contains(roleAdmin)).isTrue();
        assertThat(savedCust.getRoles().contains(roleCustomer)).isTrue();
    }

    @Test
    void createCustomerTest() {

        Role roleCustomer = roleRepository.findByName("ROLE_CUSTOMER");
        assertThat(roleCustomer).isNotNull();

        User user = userRepository.findByUsername("customer1");
        assertThat(user).isNotNull();
        assertThat(user.getRoles().contains(roleCustomer)).isTrue();

        CustomerInfo customerInfo = customerRepository.findByUserId(user.getId());
        assertThat(customerInfo).isNull();

        customerInfo = CustomerInfo.builder()
                .userId(user.getId())
                .firstName("customer1")
                .lastName("customer1")
                .creditLimit(1_000_000.0)
                .usedCreditLimit(0.0)
                .build();

        customerRepository.save(customerInfo);
        assertThat(customerInfo.getId()).isNotNull();
    }

}
