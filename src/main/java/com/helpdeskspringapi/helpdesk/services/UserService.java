package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.config.AuthorizationServerConfig;
import com.helpdeskspringapi.helpdesk.dtos.twillio.MessageRequest;
import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.exceptions.*;
import com.helpdeskspringapi.helpdesk.repositories.RoleRepository;
import com.helpdeskspringapi.helpdesk.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthService authService;
    private final AuthorizationServerConfig serverConfig;
    private final MessageSender messageSender;
    private final UserAuthService userAuthService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, AuthService authService, AuthorizationServerConfig serverConfig, MessageSender messageSender, UserAuthService userAuthService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authService = authService;
        this.serverConfig = serverConfig;
        this.messageSender = messageSender;
        this.userAuthService = userAuthService;
    }

    @Transactional(readOnly = true)
    public UserMinDTO findById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        authService.selfOrAdmin(user.getId());

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

        return page.map(UserMinDTO::new);

    }

    @Transactional(readOnly = true)
    public Page<UserMinDTO> findAllWithRoles(Pageable pageable) {

        Page<User> page = userRepository.findAll(pageable);
        userRepository.findUserWithRoles(page.stream().collect(Collectors.toSet()));

        return page.map(UserMinDTO::new);

    }

    @Transactional
    public UserDTO insert(UserInputDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email already registered");
        }
        Set<Long> ids = dto.getRoles().stream().map(x -> x.getId()).collect(Collectors.toSet());
        List<Role> roles = roleRepository.findAllById(ids);

        if (roles.size() != ids.size()) {
            throw new ResourceNotFoundException("There is 1 or more invalid id.");
        }


        User user = new User();
        user.getRoles().clear();
        user.getRoles().addAll(roles);

        copyDtoToEntity(dto, user);

        user = userRepository.save(user);

        messageSender.sendSms(
                new MessageRequest(userAuthService.getMe().getName(),
                        dto.getDdd(), dto.getPhone(),
                        "Your account has been created! Welcome, " + dto.getName() + "!" + "Your login is your email and your password"));

        return new UserDTO(user);

    }

    @Transactional
    public UserDTO update(Long id, UserInputDTO dto) {

        try {
            User user = userRepository.getReferenceById(id);

            copyDtoToEntity(dto, user);
            user = userRepository.save(user);

            return new UserDTO(user);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("User not found");
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Email already registered");
        }

    }

    @Transactional
    public void delete(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Id not found");
        }

        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity failure");
        }
    }

    private void copyDtoToEntity(UserInputDTO dto, User user) {
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setDdd(dto.getDdd());
        user.setPhone(dto.getPhone());
        user.setPassword(serverConfig.passwordEncoder().encode(dto.getPassword()));
    }


}



