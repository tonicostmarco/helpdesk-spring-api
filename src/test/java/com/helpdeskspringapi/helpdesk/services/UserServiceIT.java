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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @BeforeEach
    public void setUp() {
        assertTrue(userRepository.findByEmail(ADMIN_EMAIL).isPresent(), "admin user must be present in import.sql");
    }

    @AfterEach
    public void tearDown() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    private void authenticateAsAdmin(String email) {
        Map<String, Object> headers = Map.of("alg", "none");
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", email);
        Instant now = Instant.now();
        Jwt jwt = new Jwt("token", now, now.plus(1, ChronoUnit.HOURS), headers, claims);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(jwt, "N/A", authorities);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Long nonExistingId() {
        return userRepository.findAll().stream()
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(0L) + 9999L;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    private Role anyRole() {
        return roleRepository.findAll().stream().findFirst().orElseThrow();
    }

    private Role getRoleByAuthority(String authority) {
        return roleRepository.findAll().stream()
                .filter(role -> authority.equals(role.getAuthority()))
                .findFirst()
                .orElseThrow();
    }

    private UserInputDTO createBaseUserInput() {
        UserInputDTO input = new UserInputDTO();
        input.setName("Integration Test User");
        input.setEmail("it.user+" + UUID.randomUUID() + "@helpdesk.com");
        input.setDdd(21);
        input.setPhone("999888777");
        input.setPassword("Aa1@pass");
        return input;
    }

    private UserInputDTO createValidUserInput() {
        return createUserInputWithRoles(Set.of(roleDto(anyRole())));
    }

    private UserInputDTO createUserInputWithEmail(String email) {
        UserInputDTO input = createValidUserInput();
        input.setEmail(email);
        return input;
    }

    private UserInputDTO createUserInputWithRoles(Set<RoleDTO> roles) {
        UserInputDTO input = createBaseUserInput();
        input.setRoles(roles);
        return input;
    }

    private RoleDTO roleDto(Role role) {
        return new RoleDTO(role.getId(), role.getAuthority(), Set.of());
    }

    private void assertUserMatchesInput(UserDTO actual, UserInputDTO expected) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getDdd(), actual.getDdd());
        assertEquals(expected.getPhone(), actual.getPhone());
    }

    private void assertPasswordEncoded(String rawPassword, String encodedPassword) {
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword), "encoded password must match raw password");
    }

    @Test
    public void shouldInsertNewUserWhenCorrectData() {
        authenticateAsAdmin(ADMIN_EMAIL);

        Role role = anyRole();
        Long roleId = role.getId();

        UserInputDTO input = createUserInputWithRoles(Set.of(roleDto(role)));

        UserDTO saved = userService.insert(input);

        assertNotNull(saved);
        assertUserMatchesInput(saved, input);
        assertFalse(saved.getRoles().isEmpty());

        User dbUser = getUserByEmail(input.getEmail());
        assertEquals(saved.getId(), dbUser.getId());

        assertPasswordEncoded(input.getPassword(), dbUser.getPassword());

        boolean hasRole = dbUser.getRoles().stream().anyMatch(r -> r.getId().equals(roleId));
        assertTrue(hasRole, "expected persisted user to have assigned role");
    }

    @Test
    public void shouldThrowBusinessExceptionWhenEmailDuplicatedOnInsert() {
        authenticateAsAdmin(ADMIN_EMAIL);

        UserInputDTO input = createUserInputWithEmail(NOC_EMAIL);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.insert(input));

        assertTrue(ex.getMessage().toLowerCase().contains("email already"));
    }

    @Test
    public void shouldUpdateWhenIdExists() {
        authenticateAsAdmin(ADMIN_EMAIL);

        User userToUpdate = getUserByEmail(NOC_EMAIL);
        assertFalse(userToUpdate.hasRole("ROLE_ADMIN"), "precondition: chosen user must not have ROLE_ADMIN");
        Long id = userToUpdate.getId();

        Role adminRole = getRoleByAuthority("ROLE_ADMIN");
        UserInputDTO dto = createUserInputWithRoles(Set.of(roleDto(adminRole)));
        dto.setId(id);
        dto.setName("NOC Updated");
        dto.setEmail("noc.updated+" + UUID.randomUUID() + "@helpdesk.com");
        dto.setDdd(31);
        dto.setPhone("111222333");
        dto.setPassword("Aa1@NewPass");

        Set<String> originalRoles = userToUpdate.getRoles().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet());

        UserDTO result = userService.update(id, dto);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertUserMatchesInput(result, dto);

        Set<String> resultRoles = result.getRoles().stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.toSet());
        assertEquals(originalRoles, resultRoles, "update should not change existing roles");

        User fromDb = userRepository.findById(id).orElseThrow();
        assertEquals(dto.getName(), fromDb.getName());
        assertTrue(passwordEncoder.matches(dto.getPassword(), fromDb.getPassword()));
        Set<String> rolesFromDb = fromDb.getRoles().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet());
        assertEquals(originalRoles, rolesFromDb, "roles in DB must remain unchanged after update");
    }

    @Test
    public void shouldThrowResourceNotFoundWhenUpdateIdDoesNotExist() {
        UserInputDTO dto = createUserInputWithRoles(Set.of(roleDto(getRoleByAuthority("ROLE_CLIENT"))));
        dto.setName("Doesn't matter");
        dto.setEmail("doesnotexist@helpdesk.com");
        dto.setDdd(11);
        dto.setPhone("0000000");
        dto.setPassword("Aa1@pass");

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.update(nonExistingId(), dto));
        assertTrue(ex.getMessage().toLowerCase().contains("user not found"));
    }

    @Test
    public void shouldDeleteWhenIdExists() {
        User user = getUserByEmail(SUPPORT_EMAIL);
        Long id = user.getId();

        assertTrue(userRepository.existsById(id));

        userService.delete(id);

        assertFalse(userRepository.existsById(id));
    }

    @Test
    public void shouldThrowResourceNotFoundWhenDeleteNonExisting() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.delete(nonExistingId()));

        assertEquals("Id not found", ex.getMessage());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional(
            propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED
    )
    public void shouldThrowDatabaseExceptionWhenDeletingUserWithTickets() {
        User user = getUserByEmail(ANA_EMAIL);
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
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () -> userService.findByName("   "));
        assertTrue(ex.getMessage().toLowerCase().contains("name required"));
    }
}