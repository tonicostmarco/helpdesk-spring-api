package com.helpdeskspringapi.helpdesk.repositories;

import com.helpdeskspringapi.helpdesk.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    //adicionar metodo para retornar usuario + o campo

}
