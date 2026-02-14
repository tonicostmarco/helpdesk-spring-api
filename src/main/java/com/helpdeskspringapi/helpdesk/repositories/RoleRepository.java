package com.helpdeskspringapi.helpdesk.repositories;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleDTO;
import com.helpdeskspringapi.helpdesk.dtos.role.RoleMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT obj " +
            "FROM Role obj " +
            "JOIN FETCH obj.users " +
            "WHERE obj IN :roles")
    List<RoleDTO> findAllWithUsers(List<Role> roles);

}
