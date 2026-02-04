package com.helpdeskspringapi.helpdesk.repositories;

import com.helpdeskspringapi.helpdesk.dtos.UserDTO;
import com.helpdeskspringapi.helpdesk.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new com.helpdeskspringapi.helpdesk.entities.User(obj.name) " +
            "FROM User obj " +
            "WHERE UPPER(obj.name) LIKE UPPER(CONCAT(:name, '%'))")
    Page<User> findByNameContainingIgnoreCase(Pageable pageable, String name);

}
