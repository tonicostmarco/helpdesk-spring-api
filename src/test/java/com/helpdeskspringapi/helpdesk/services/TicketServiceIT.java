package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketPatchDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketPriority;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus;
import com.helpdeskspringapi.helpdesk.exceptions.BusinessException;
import com.helpdeskspringapi.helpdesk.exceptions.ForbiddenException;
import com.helpdeskspringapi.helpdesk.exceptions.InvalidParameterException;
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.repositories.CategoryRepository;
import com.helpdeskspringapi.helpdesk.repositories.RoleRepository;
import com.helpdeskspringapi.helpdesk.repositories.TicketRepository;
import com.helpdeskspringapi.helpdesk.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class TicketServiceIT {

    private static final String OWNER_EMAIL = "it.owner." + "@helpdesk.com";
    private static final String OTHER_CLIENT_EMAIL = "it.other." + "@helpdesk.com";

    @Autowired
    private TicketService service;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockitoBean(name = "twilioMessageSenderService")
    private MessageSender messageSender;

    private User owner;
    private User otherClient;
    private Category category;
    private Ticket ownerTicket;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20);

        owner = createClientUser("Integration Owner", uniqueEmail(OWNER_EMAIL));
        otherClient = createClientUser("Integration Other", uniqueEmail(OTHER_CLIENT_EMAIL));
        category = createCategory("IT-DNS-" + UUID.randomUUID(), "Integration DNS category");

        ownerTicket = createTicket(
                owner,
                "Integration Ticket " + UUID.randomUUID(),
                "Primary ticket description for integration test",
                TicketPriority.MEDIUM,
                TicketStatus.OPEN,
                Instant.parse("2026-03-25T10:00:00Z")
        );

        authenticateAs(owner.getEmail());
    }

    private String uniqueEmail(String prefix) {
        return prefix.replace("@", "+" + UUID.randomUUID() + "@");
    }

    private User createClientUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setDdd(11);
        user.setPhone("9" + String.format("%08d", Math.abs(email.hashCode()) % 100000000));
        user.setPassword("$2b$10$8orZfrgp/uRwNstcqzYmI.jtGSlcpLEugS0xk1wefRW2KUOkEuuf2");
        Role clientRole = roleRepository.findByAuthority("ROLE_CLIENT")
                .orElseThrow(() -> new IllegalStateException("ROLE_CLIENT not found in test database"));
        user.addRole(clientRole);
        return userRepository.save(user);
    }

    private Category createCategory(String name, String description) {
        Category entity = new Category();
        entity.setName(name);
        entity.setDescription(description);
        return categoryRepository.save(entity);
    }

    private Ticket createTicket(User client,
                                String title,
                                String description,
                                TicketPriority priority,
                                TicketStatus status,
                                Instant createdAt) {
        Ticket ticket = new Ticket();
        ticket.setClient(client);
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setStatus(status);
        ticket.setCreatedAt(createdAt);
        ticket.setUpdatedAt(createdAt);
        ticket.getCategories().add(category);
        return ticketRepository.save(ticket);
    }

    private void authenticateAs(String email) {
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("username", email)
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                jwt,
                "N/A",
                Set.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Long nonExistingTicketId() {
        return ticketRepository.findAll().stream().map(Ticket::getId).max(Long::compareTo).orElse(0L) + 1000L;
    }

    private TicketInputDTO validInputWithCategory(Category categoryEntity) {
        TicketInputDTO dto = new TicketInputDTO();
        dto.setTitle("Inserted ticket " + UUID.randomUUID());
        dto.setDescription("Ticket created by integration test");

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryEntity.getId());
        categoryDTO.setName(categoryEntity.getName());
        dto.setCategories(Set.of(categoryDTO));
        return dto;
    }

    @Test
    public void shouldReturnTicketMinDTOWhenIdExists() {
        TicketMinDTO result = service.findById(ownerTicket.getId());

        assertNotNull(result);
        assertEquals(ownerTicket.getId(), result.getId());
        assertEquals(ownerTicket.getTitle(), result.getTitle());
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionTicketWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingTicketId()));
    }

    @Test
    public void shouldReturnTicketPageable() {
        Page<TicketMinDTO> result = service.findAll(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.getContent().stream().anyMatch(ticket -> ticket.getId().equals(ownerTicket.getId())));
    }

    @Test
    public void shouldReturnTicketWithUsersPageable() {
        Page<TicketMinDTO> result = service.findAllWithUsers(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.getContent().stream().allMatch(ticket -> ticket.getClient() != null));
    }

    @Test
    public void shouldReturnTicketByTitle() {
        String fragment = ownerTicket.getTitle().substring(0, 12);

        var result = service.findByTitle(fragment);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(ticket -> ticket.getId().equals(ownerTicket.getId())));
    }

    @Test
    public void shouldThrowInvalidParameterExceptionWhenTitleIsBlank() {
        assertThrows(InvalidParameterException.class, () -> service.findByTitle("   "));
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenTitleDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> service.findByTitle("not-found-" + UUID.randomUUID()));
    }

    @Test
    public void shouldThrowInvalidParameterExceptionWhenCategoryIsBlank() {
        assertThrows(InvalidParameterException.class, () -> service.findByCategory("  "));
    }

    @Test
    public void shouldReturnTicketByCategory() {
        var result = service.findByCategory(category.getName());

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(ticket -> ticket.getId().equals(ownerTicket.getId())));
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> service.findByCategory("category-not-found-" + UUID.randomUUID()));
    }

    @Test
    public void shouldReturnTicketWhenFindMe() {
        TicketMinDTO result = service.findMe(ownerTicket.getId());

        assertNotNull(result);
        assertEquals(ownerTicket.getId(), result.getId());
        assertEquals(owner.getId(), result.getClient().getId());
    }

    @Test
    public void shouldThrowForbiddenExceptionWhenFindMeFromDifferentClient() {
        authenticateAs(otherClient.getEmail());

        assertThrows(ForbiddenException.class, () -> service.findMe(ownerTicket.getId()));
    }

    @Test
    public void shouldReturnOldestTicketFirst() {
        Ticket older = createTicket(
                owner,
                "Oldest ticket " + UUID.randomUUID(),
                "Oldest ordering test ticket",
                TicketPriority.LOW,
                TicketStatus.OPEN,
                Instant.parse("2026-03-25T08:00:00Z")
        );
        Ticket newer = createTicket(
                owner,
                "Newer ticket " + UUID.randomUUID(),
                "Newer ordering test ticket",
                TicketPriority.HIGH,
                TicketStatus.OPEN,
                Instant.parse("2026-03-25T09:00:00Z")
        );

        Page<TicketMinDTO> result = service.findOldestFirst(PageRequest.of(0, 50));

        assertFalse(result.isEmpty());

        var ordered = result.getContent();
        for (int i = 1; i < ordered.size(); i++) {
            assertFalse(ordered.get(i - 1).getCreatedAt().isAfter(ordered.get(i).getCreatedAt()));
        }

        int olderIndex = -1;
        int newerIndex = -1;
        for (int i = 0; i < ordered.size(); i++) {
            if (ordered.get(i).getId().equals(older.getId())) olderIndex = i;
            if (ordered.get(i).getId().equals(newer.getId())) newerIndex = i;
        }
        assertTrue(olderIndex != -1, "Older ticket not found in result");
        assertTrue(newerIndex != -1, "Newer ticket not found in result");
        assertTrue(olderIndex < newerIndex, "Older ticket should appear before newer ticket");
    }

    @Test
    public void shouldInsertNewTicketWhenCorrectData() {
        TicketInputDTO input = validInputWithCategory(category);

        TicketMinDTO result = service.insert(input);

        assertNotNull(result);
        assertEquals(input.getTitle(), result.getTitle());
        Ticket persisted = ticketRepository.findById(result.getId()).orElseThrow();
        assertEquals(owner.getId(), persisted.getClient().getId());
        assertEquals(TicketStatus.OPEN, persisted.getStatus());
        assertEquals(TicketPriority.LOW, persisted.getPriority());
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionInInsertMethodWhenWrongCategories() {
        TicketInputDTO input = validInputWithCategory(category);
        CategoryDTO wrongCategory = new CategoryDTO();
        wrongCategory.setId(category.getId() + 9999L);
        wrongCategory.setName("Wrong");
        input.setCategories(Set.of(wrongCategory));

        assertThrows(ResourceNotFoundException.class, () -> service.insert(input));
    }

    @Test
    public void shouldUpdateTicketWhenCorrectData() {
        TicketPatchDTO patch = new TicketPatchDTO();
        patch.setStatus(TicketStatus.CLOSED);
        patch.setPriority(TicketPriority.HIGH);

        TicketMinDTO result = service.update(ownerTicket.getId(), patch);

        assertNotNull(result);
        assertEquals(ownerTicket.getId(), result.getId());
        assertEquals(TicketStatus.CLOSED, result.getStatus());
        assertEquals(TicketPriority.HIGH, result.getPriority());
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionInUpdateMethodWhenWrongId() {
        TicketPatchDTO patch = new TicketPatchDTO();
        patch.setStatus(TicketStatus.CLOSED);
        patch.setPriority(TicketPriority.HIGH);

        assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingTicketId(), patch));
    }

    @Test
    public void shouldUpdateStatusWhenCorrectIdAndData() {
        TicketPatchDTO patch = new TicketPatchDTO();
        patch.setStatus(TicketStatus.CLOSED);

        TicketMinDTO result = service.patchStatus(ownerTicket.getId(), patch);

        assertNotNull(result);
        assertEquals(TicketStatus.CLOSED, result.getStatus());
    }

    @Test
    public void shouldThrowBusinessExceptionInPatchStatusWhenInvalidStatus() {
        TicketPatchDTO patch = new TicketPatchDTO();
        patch.setStatus(ownerTicket.getStatus());

        assertThrows(BusinessException.class, () -> service.patchStatus(ownerTicket.getId(), patch));
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionInPatchStatusWhenIdDoesNotExist() {
        TicketPatchDTO patch = new TicketPatchDTO();
        patch.setStatus(TicketStatus.CLOSED);

        assertThrows(ResourceNotFoundException.class, () -> service.patchStatus(nonExistingTicketId(), patch));
    }

    @Test
    public void shouldThrowBusinessExceptionInPatchPriorityWhenInvalidStatus() {
        TicketPatchDTO patch = new TicketPatchDTO();
        patch.setPriority(ownerTicket.getPriority());

        assertThrows(BusinessException.class, () -> service.patchPriority(ownerTicket.getId(), patch));
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionInPatchPriorityWhenIdDoesNotExist() {
        TicketPatchDTO patch = new TicketPatchDTO();
        patch.setPriority(TicketPriority.HIGH);

        assertThrows(ResourceNotFoundException.class, () -> service.patchPriority(nonExistingTicketId(), patch));
    }

    @Test
    public void shouldDeleteTicketWhenIdExistsAndTicketIsClosed() {
        ownerTicket.setStatus(TicketStatus.CLOSED);
        ticketRepository.save(ownerTicket);

        service.delete(ownerTicket.getId());

        assertFalse(ticketRepository.existsById(ownerTicket.getId()));
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingTicketId()));
    }

    @Test
    public void shouldThrowBusinessExceptionWhenStatusIsNotClosed() {
        assertThrows(BusinessException.class, () -> service.delete(ownerTicket.getId()));
    }

}