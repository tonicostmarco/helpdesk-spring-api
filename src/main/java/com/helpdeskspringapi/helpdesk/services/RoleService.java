package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.repositories.RoleRepository;
import com.helpdeskspringapi.helpdesk.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public RoleDTO findById(Long id) {

        Role role = roleRepository.findById(id).orElseThrow();

        return new RoleDTO(role);

    }

    @Transactional(readOnly = true)
    public Page<RoleDTO> findAll(Pageable pageable) {

        Page<Role> roles = roleRepository.findAll(pageable);

        return roles.map(RoleDTO::new);

    }

    @Transactional
    public RoleDTO insert(RoleDTO dto) {

        Role role = new Role();
        copyDtoToEntity(dto, role);

        role = roleRepository.save(role);

        return new RoleDTO(role);

    }

    @Transactional
    public RoleDTO update(Long id, RoleDTO dto) {

        Role role = roleRepository.getReferenceById(id);

        copyDtoToEntity(dto, role);

        role = roleRepository.save(role);

        return new RoleDTO(role);

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
