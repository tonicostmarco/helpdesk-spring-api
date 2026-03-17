package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.exceptions.*;
import com.helpdeskspringapi.helpdesk.factory.UserFactory;
import com.helpdeskspringapi.helpdesk.repositories.RoleRepository;
import com.helpdeskspringapi.helpdesk.config.AuthorizationServerConfig;
import com.helpdeskspringapi.helpdesk.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthService authService;

    @Mock
    private AuthorizationServerConfig serverConfig;

    @Mock
    private MessageSender messageSender;

    @Mock
    private UserAuthService userAuthService;

    private Long existingId;
    private Long nonExistingId;

    private User user;
    private UserMinDTO userMinDTO;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        service = new UserService(
                userRepository,
                roleRepository,
                authService,
                serverConfig,
                messageSender,
                userAuthService
        );

        existingId = 1L;
        nonExistingId = 100L;

        user = UserFactory.createUser();
        user.setId(existingId);
        userMinDTO = UserFactory.createUserMinDTO();

        pageable = PageRequest.of(0, 10);

        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(user)));

        when(serverConfig.passwordEncoder()).thenReturn(new BCryptPasswordEncoder());

        when(userAuthService.getMe()).thenReturn(new UserDTO(user));

        when(userRepository.findByName(anyString())).thenReturn(Set.of(userMinDTO));

        when(userRepository.findAll()).thenReturn(List.of(user));

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        when(roleRepository.findAllById(anySet())).thenReturn(List.of(new Role(1L, "ROLE_ADMIN")));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            if (u.getId() == null) u.setId(2L);
            return u;
        });

        doNothing().when(messageSender).sendSms(any());

        when(userRepository.getReferenceById(existingId)).thenReturn(user);
        when(userRepository.getReferenceById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(userRepository.existsById(existingId)).thenReturn(true);
        when(userRepository.existsById(nonExistingId)).thenReturn(false);

    }

    // ---------- FIND BY ID ----------

    @Test
    public void shouldReturnUserWhenIdExists() {
        UserMinDTO dto = service.findById(existingId);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(existingId, dto.getId());
        verify(userRepository).findById(existingId);
    }

    @Test
    public void shouldThrowResourceNotFoundWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
        verify(userRepository).findById(nonExistingId);
    }

    // ---------- FIND BY NAME ----------

    @Test
    public void shouldReturnUsersByName() {
        String name = user.getName();
        Set<UserMinDTO> result = service.findByName(name);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        verify(userRepository).findByName(name);
    }

    @Test
    public void shouldThrowInvalidParameterWhenNameBlank() {
        Assertions.assertThrows(InvalidParameterException.class, () -> service.findByName("   "));
    }

    // ---------- PAGING / FIND ALL ----------

    @Test
    public void shouldReturnPagedUsers() {
        Page<UserMinDTO> page = service.findAll(pageable);
        Assertions.assertNotNull(page);
        verify(userRepository).findAll(pageable);
    }

    @Test
    public void shouldReturnAllWithRoles() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.findUserWithRoles(users)).thenReturn(List.of(userMinDTO));

        List<UserMinDTO> list = service.findAllWithRoles();

        Assertions.assertNotNull(list);
        Assertions.assertFalse(list.isEmpty());
        verify(userRepository).findAll();
        verify(userRepository).findUserWithRoles(users);
    }

    // ---------- INSERT / UPDATE / DELETE ----------

    @Test
    public void shouldInsertUserWhenCorrectData() {
        UserInputDTO dto = UserFactory.createUserInputDTO();

        RoleDTO r = new RoleDTO();
        r.setId(1L);
        dto.getRoles().add(r);

        UserDTO result = service.insert(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(dto.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(dto.getEmail());
        verify(roleRepository).findAllById(Set.of(1L));
        verify(userRepository).save(any(User.class));
        verify(messageSender).sendSms(any());
    }

    @Test
    public void shouldThrowBusinessExceptionWhenEmailAlreadyRegisteredOnInsert() {
        UserInputDTO dto = UserFactory.createUserInputDTO();
        RoleDTO r = new RoleDTO();
        r.setId(1L);
        dto.getRoles().add(r);

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        Assertions.assertThrows(BusinessException.class, () -> service.insert(dto));

        verify(userRepository).existsByEmail(dto.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowMessageExceptionWhenSmsFailsOnInsert() {
        UserInputDTO dto = UserFactory.createUserInputDTO();
        RoleDTO r = new RoleDTO();
        r.setId(1L);
        dto.getRoles().add(r);


        doThrow(new RuntimeException("twilio")).when(messageSender).sendSms(any());

        Assertions.assertThrows(MessageException.class, () -> service.insert(dto));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void shouldUpdateWhenIdExists() {
        UserInputDTO dto = UserFactory.createUserInputDTO();
        UserDTO result = service.update(existingId, dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(dto.getName(), result.getName());
        Assertions.assertEquals(dto.getEmail(), result.getEmail());
        Assertions.assertEquals(dto.getDdd(), result.getDdd());
        Assertions.assertEquals(dto.getPhone(), result.getPhone());
        verify(userRepository).getReferenceById(existingId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void shouldThrowResourceNotFoundWhenUpdateIdDoesNotExist() {
        UserInputDTO dto = UserFactory.createUserInputDTO();
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, dto));
    }

    @Test
    public void shouldDeleteWhenIdExists() {
        service.delete(existingId);

        verify(userRepository).existsById(existingId);
        verify(userRepository).deleteById(existingId);
    }

    @Test
    public void shouldThrowResourceNotFoundWhenDeleteIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
        verify(userRepository).existsById(nonExistingId);
        verify(userRepository, never()).deleteById(nonExistingId);
    }

    @Test
    public void shouldThrowDatabaseExceptionWhenDeleteDependentId() {
        Long dependentId = 3L;

        when(userRepository.existsById(3L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("fk violation")).when(userRepository).deleteById(3L);
        Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentId));
        verify(userRepository).deleteById(dependentId);
    }

}
