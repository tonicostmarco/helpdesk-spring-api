package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.exceptions.BusinessException;
import com.helpdeskspringapi.helpdesk.exceptions.InvalidParameterException;
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.repositories.RoleRepository;
import com.helpdeskspringapi.helpdesk.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public UserMinDTO findById(Long id) {

        if (id == null) {
            throw new InvalidParameterException("Id required");
        }

              User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));

            return new UserMinDTO(user);

    }

    @Transactional(readOnly = true)
    public Set<UserMinDTO> findByName(String name) {

        if (name.isBlank()) {
            throw new InvalidParameterException("Name required");
        }

        try {
            return userRepository.findByName(name);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Name not found");
        }
    }

    @Transactional(readOnly = true)
    public Page<UserMinDTO> findAll(Pageable pageable) {

        Page<User> page = userRepository.findAll(pageable);
        userRepository.findUserWithRoles(page.stream().collect(Collectors.toSet()));

        return page.map(UserMinDTO::new);

    }

    @Transactional
    public UserDTO insert(UserInputDTO dto) {

        User user = new User();
        copyDtoToEntity(dto, user);

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email already registered");
        }

                  Set<Long> ids = dto.getRoles().stream().map(x -> x.getId()).collect(Collectors.toSet());

            List<Role> roles = roleRepository.findAllById(ids);


            if (roles.size() != ids.size()) {
                throw new ResourceNotFoundException("There is 1 or more invalid id.");
            }

            user.getRoles().clear();
            user.getRoles().addAll(roles);

            user = userRepository.save(user);
            return new UserDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserInputDTO dto) {

        try {
            User user = userRepository.getReferenceById(id);

            copyDtoToEntity(dto, user);
            user = userRepository.save(user);

            return new UserDTO(user);
        }
        catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("User not found");
        }

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



