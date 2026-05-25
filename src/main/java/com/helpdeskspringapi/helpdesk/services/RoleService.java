package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleDTO;
import com.helpdeskspringapi.helpdesk.dtos.role.RoleMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
/*
import com.helpdeskspringapi.helpdesk.exceptions.DatabaseException;
import com.helpdeskspringapi.helpdesk.exceptions.InvalidParameterException;
import org.springframework.dao.DataIntegrityViolationException;
*/
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.repositories.RoleRepository;
import com.helpdeskspringapi.helpdesk.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {


    private final RoleRepository roleRepository;;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;

    }

    @Transactional(readOnly = true)
    public RoleDTO findById(Long id) {

        Role role = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        return new RoleDTO(role);

    }

    @Transactional(readOnly = true)
    public List<RoleMinDTO> findAll() {

        List<Role> roles = roleRepository.findAll();

        return roles.stream().map(RoleMinDTO::new).collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<RoleDTO> findAllWithUsers() {

        List<Role> roles = roleRepository.findAll();
        return roleRepository.findAllWithUsers(roles.stream().collect(Collectors.toList()));

    }


}
