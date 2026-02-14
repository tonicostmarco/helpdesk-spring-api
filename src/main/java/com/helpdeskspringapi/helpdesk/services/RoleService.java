package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleDTO;
import com.helpdeskspringapi.helpdesk.dtos.role.RoleMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.exceptions.DatabaseException;
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.repositories.RoleRepository;
import com.helpdeskspringapi.helpdesk.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

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

    @Transactional
    public RoleMinDTO insert(RoleDTO dto) {

        if (roleRepository.existsById(dto.getId())) {
            throw new DatabaseException("Role already registered");
        }

            Role role = new Role();
            copyDtoToEntity(dto, role);

            role = roleRepository.save(role);

            return new RoleMinDTO(role);




    }

    @Transactional
    public RoleMinDTO update(Long id, RoleDTO dto) {

        Role role = roleRepository.getReferenceById(id);

        copyDtoToEntity(dto, role);

        role = roleRepository.save(role);

        return new RoleMinDTO(role);

    }

    @Transactional
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    private void copyDtoToEntity(RoleDTO dto, Role role) {
        role.setId(dto.getId());
        role.setAuthority(dto.getAuthority());
      }

}
