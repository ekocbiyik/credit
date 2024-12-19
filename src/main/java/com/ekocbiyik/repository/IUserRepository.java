package com.ekocbiyik.repository;

import com.ekocbiyik.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Query("from User where id =:userId")
    User findUserById(@Param("userId") Long userId);

}
