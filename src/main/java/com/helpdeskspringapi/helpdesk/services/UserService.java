package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.RoleDTO;
import com.helpdeskspringapi.helpdesk.dtos.UserInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.UserDTO;
import com.helpdeskspringapi.helpdesk.dtos.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.repositories.RoleRepository;
import com.helpdeskspringapi.helpdesk.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {

        User user = userRepository.findById(id).orElseThrow();

        return new UserDTO(user);

    }

    @Transactional(readOnly = true)
    public Page<UserMinDTO> findByName(Pageable pageable, String name) {
        Page<User> users = userRepository.findByNameContainingIgnoreCase(pageable, name);

        return users.map(UserMinDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        return users.map(UserDTO::new);

    }

    @Transactional
    public UserDTO insert(UserInputDTO dto) {

        User user = new User();
        copyDtoToEntity(dto, user);

        for (RoleDTO roleDTO : dto.getRoles()) {

            Role role = roleRepository.getReferenceById(roleDTO.getId());
            user.getRoles().add(role);
        }
        user = userRepository.save(user);

        return new UserDTO(user);

    }

    @Transactional
    public UserDTO update(Long id, UserInputDTO dto) {

        User user = userRepository.getReferenceById(id);

        copyDtoToEntity(dto, user);

        user = userRepository.save(user);

        return new UserDTO(user);

    }


    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }


    private void copyDtoToEntity(UserInputDTO dto, User user) {
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
    }

}



