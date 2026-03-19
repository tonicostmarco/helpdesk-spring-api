package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.exceptions.BusinessException;
import com.helpdeskspringapi.helpdesk.exceptions.DatabaseException;
import com.helpdeskspringapi.helpdesk.exceptions.InvalidParameterException;
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.repositories.RoleRepository;
import com.helpdeskspringapi.helpdesk.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIT {

    private static final String ADMIN_EMAIL = "admin@helpdesk.com";

    private static final String NOC_EMAIL = "noc@helpdesk.com";

    private static final String SUPPORT_EMAIL = "support@helpdesk.com";

    private static final String ANA_NAME = "Ana Client";

    private static final String ANA_EMAIL = "ana.client@helpdesk.com";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean(name = "twilioMessageSenderService")
    private MessageSender messageSender;

    private Long adminUserId;
    private List<User> allUsers;

    @BeforeEach
    public void setUp() {

        allUsers = userRepository.findAll();
        Optional<User> adminOpt = userRepository.findByEmail(ADMIN_EMAIL);
        assertTrue(adminOpt.isPresent(), "admin user must be present in import.sql");
        adminUserId = adminOpt.get().getId();
    }

    @AfterEach
    public void tearDown() {

        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }


    private void authenticateAs(String email) {
        Map<String, Object> headers = Map.of("alg", "none");
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", email);
        Instant now = Instant.now();
        Jwt jwt = new Jwt("token", now, now.plus(1, ChronoUnit.HOURS), headers, claims);


        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(jwt, "N/A", authorities);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void shouldInsertNewUserWhenCorrectData() {

        authenticateAs(ADMIN_EMAIL);


        Role role = roleRepository.findAll().stream().findFirst().orElseThrow();
        Long roleId = role.getId();

        UserInputDTO input = new UserInputDTO();
        input.setName("Integration Test User");
        String newEmail = "it.user+" + UUID.randomUUID().toString() + "@helpdesk.com";
        input.setEmail(newEmail);
        input.setDdd(21);
        input.setPhone("999888777");
        input.setPassword("Aa1@pass"); // meets the pattern
        input.setRoles(new HashSet<>(Set.of(new RoleDTO(roleId, role.getAuthority(), Set.of()))));

        UserDTO saved = userService.insert(input);

        assertNotNull(saved);
        assertEquals(input.getName(), saved.getName());
        assertEquals(input.getEmail(), saved.getEmail());
        assertEquals(input.getDdd(), saved.getDdd());
        assertEquals(input.getPhone(), saved.getPhone());
        assertFalse(saved.getRoles().isEmpty());


        Optional<User> dbUserOpt = userRepository.findByEmail(newEmail);
        assertTrue(dbUserOpt.isPresent());
        User dbUser = dbUserOpt.get();
        assertEquals(saved.getId(), dbUser.getId());

        assertNotEquals(input.getPassword(), dbUser.getPassword());
        assertTrue(passwordEncoder.matches(input.getPassword(), dbUser.getPassword()), "encoded password must match raw password");

        boolean hasRole = dbUser.getRoles().stream().anyMatch(r -> r.getId().equals(roleId));
        assertTrue(hasRole, "expected persisted user to have assigned role");
    }

    @Test
    public void shouldThrowBusinessExceptionWhenEmailDuplicatedOnInsert() {
        authenticateAs(ADMIN_EMAIL);


        UserInputDTO input = new UserInputDTO();
        input.setName("Duplicate");
        input.setEmail(NOC_EMAIL);
        input.setDdd(11);
        input.setPhone("900000002");
        input.setPassword("Aa1@pass");

        Role r = roleRepository.findAll().stream().findAny().orElseThrow();
        input.setRoles(new HashSet<>(Set.of(new RoleDTO(r.getId(), r.getAuthority(), Set.of()))));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            userService.insert(input);
        });

        assertTrue(ex.getMessage().toLowerCase().contains("email already"));
    }

    @Test
    public void shouldUpdateWhenIdExists() {

        authenticateAs(ADMIN_EMAIL);


        User userToUpdate = userRepository.findByEmail(NOC_EMAIL).orElseThrow();
        assertFalse(userToUpdate.hasRole("ROLE_ADMIN"), "precondition: chosen user must not have ROLE_ADMIN");
        Long id = userToUpdate.getId();


        UserInputDTO dto = new UserInputDTO();
        dto.setId(id);
        dto.setName("NOC Updated");
        dto.setEmail("noc.updated+" + UUID.randomUUID().toString() + "@helpdesk.com");
        dto.setDdd(31);
        dto.setPhone("111222333");
        dto.setPassword("Aa1@NewPass");

        dto.setRoles(new HashSet<>(Set.of(new RoleDTO(1L, "ROLE_ADMIN", Set.of()))));


        Set<String> originalRoles = userToUpdate.getRoles().stream().map(Role::getAuthority).collect(java.util.stream.Collectors.toSet());

        UserDTO result = userService.update(id, dto);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getDdd(), result.getDdd());
        assertEquals(dto.getPhone(), result.getPhone());

        Set<String> resultRoles = result.getRoles().stream().map(r -> r.getAuthority()).collect(java.util.stream.Collectors.toSet());
        assertEquals(originalRoles, resultRoles, "update should not change existing roles");

        User fromDb = userRepository.findById(id).orElseThrow();
        assertEquals(dto.getName(), fromDb.getName());
        assertTrue(passwordEncoder.matches(dto.getPassword(), fromDb.getPassword()));
        Set<String> rolesFromDb = fromDb.getRoles().stream().map(Role::getAuthority).collect(java.util.stream.Collectors.toSet());
        assertEquals(originalRoles, rolesFromDb, "roles in DB must remain unchanged after update");
    }

    @Test
    public void shouldThrowResourceNotFoundWhenUpdateIdDoesNotExist() {

        UserInputDTO dto = new UserInputDTO();
        dto.setName("Doesn't matter");
        dto.setEmail("doesnotexist@helpdesk.com");
        dto.setDdd(11);
        dto.setPhone("0000000");
        dto.setPassword("Aa1@pass");
        dto.setRoles(new HashSet<>(Set.of(new RoleDTO(4L, "ROLE_CLIENT", Set.of()))));

        Long nonExistingId = allUsers.stream()
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(0L) + 9999L;

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            userService.update(nonExistingId, dto);
        });
        assertTrue(ex.getMessage().toLowerCase().contains("user not found"));
    }

    @Test
    public void shouldDeleteWhenIdExists() {

        User user = userRepository.findByEmail(SUPPORT_EMAIL).orElseThrow();
        Long id = user.getId();


        assertTrue(userRepository.existsById(id));

        userService.delete(id);

        assertFalse(userRepository.existsById(id));
    }

    @Test
    public void shouldThrowResourceNotFoundWhenDeleteNonExisting() {
        Long nonExistingId = allUsers.stream()
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(0L) + 9999L;

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            userService.delete(nonExistingId);
        });

        assertEquals("Id not found", ex.getMessage());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional(
            propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED
    )
    public void shouldThrowDatabaseExceptionWhenDeletingUserWithTickets() {

        User user = userRepository.findByEmail(ANA_EMAIL).orElseThrow();
        Long id = user.getId();

        assertThrows(DatabaseException.class, () -> userService.delete(id));
    }

    @Test
    public void shouldReturnByNameWhenValid() {

        Set<UserMinDTO> result = userService.findByName("Ana");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        boolean found = result.stream().anyMatch(u -> ANA_NAME.equals(u.getName()));
        assertTrue(found, "expected to find 'Ana Client' when searching by name 'Ana'");
    }

    @Test
    public void shouldThrowInvalidParameterExceptionWhenNameBlank() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () -> {
            userService.findByName("   ");
        });
        assertTrue(ex.getMessage().toLowerCase().contains("name required"));
    }
}