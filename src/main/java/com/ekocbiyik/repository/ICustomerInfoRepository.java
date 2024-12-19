package com.ekocbiyik.repository;

import com.ekocbiyik.model.entities.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface ICustomerInfoRepository extends JpaRepository<CustomerInfo, Long> {

    CustomerInfo findByUserId(Long userId);

}
