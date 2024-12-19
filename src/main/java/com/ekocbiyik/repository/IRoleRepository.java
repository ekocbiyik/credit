package com.ekocbiyik.repository;

import com.ekocbiyik.model.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    @Query("from Role r where r.name in (:nameList)")
    List<Role> findAllByName(List<String> nameList);

}
