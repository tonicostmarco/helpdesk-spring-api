package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.factory.CategoryFactory;
import com.helpdeskspringapi.helpdesk.factory.TicketFactory;
import com.helpdeskspringapi.helpdesk.repositories.CategoryRepository;
import com.helpdeskspringapi.helpdesk.repositories.TicketRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class TicketServiceTest {

    @InjectMocks
    private TicketService service;

    private Long existingId;
    private Long nonExistingId;
    private Set<Long> ids;
    private Set<Long> wrongIds;
    private String expectedTitle;
    private String nonExistingTitle;
    private String expectedCategory;
    private String nonExistingCategory;

    private Ticket ticket;
    private TicketMinDTO minDTO;
    private TicketInputDTO inputDTO;
    private List<TicketMinDTO> listMinDTO;
    private List<Category> categories;

    private PageImpl<Ticket> page;
    private PageImpl<TicketMinDTO> pageMin;
    private Pageable pageable;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AuthService authService;

    @Mock
    private MessageSender messageSender;

    @BeforeEach
    void setUp() {

        existingId = 1L;
        nonExistingId = 100L;
        expectedTitle = "Internet cai do nada";
        nonExistingTitle = "nonExistingTitle";
        expectedCategory = "DNS";
        nonExistingCategory = "nonExistingCategory";

        ticket = TicketFactory.createTicket();
        minDTO = TicketFactory.createTicketMinDTO();
        inputDTO = TicketFactory.createTicketInputDTO();

        listMinDTO = List.of(minDTO);

        Category cat = CategoryFactory.createCategory();
        categories = List.of(cat);

        page = new PageImpl<>(List.of(ticket));
        pageMin = new PageImpl<>(List.of(minDTO));
        pageable = PageRequest.of(0, 10);

        ids = inputDTO.getCategories().stream().map(CategoryDTO::getId).collect(Collectors.toSet());
        wrongIds = inputDTO.getCategories().stream().map(x -> x.getId() + 7L).collect(Collectors.toSet());

        when(ticketRepository.findById(existingId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(ticketRepository.findByTitleContainingIgnoreCase(pageable, expectedTitle)).thenReturn(pageMin);
        when(ticketRepository.findByTitleContainingIgnoreCase(pageable, nonExistingTitle)).thenThrow(ResourceNotFoundException.class);

        when(ticketRepository.findByCategoryContainingIgnoreCase(expectedCategory)).thenReturn(listMinDTO);
        when(ticketRepository.findByCategoryContainingIgnoreCase(nonExistingCategory)).thenThrow(ResourceNotFoundException.class);

        when(categoryRepository.findAllById(ids)).thenReturn(categories);
        when(categoryRepository.findAllById(wrongIds)).thenReturn(List.of());

        when(ticketRepository.findAll(pageable)).thenReturn(page);
        when(ticketRepository.findAllWithUsers(pageable)).thenReturn(pageMin);
        when(ticketRepository.findAllOldestFirst(pageable)).thenReturn(pageMin);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User authenticatedUser = ticket.getClient();
        when(userAuthService.authenticated()).thenReturn(authenticatedUser);
    }

    @Test
    public void shouldReturnTicketMinDTOWhenIdExists() {

        minDTO = service.findById(existingId);

        Assertions.assertNotNull(minDTO);
        Assertions.assertEquals(existingId, minDTO.getId());
        verify(ticketRepository).findById(existingId);
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionTicketWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
        verify(ticketRepository).findById(nonExistingId);
    }

    @Test
    public void shouldReturnTicketPageable() {

        Page<TicketMinDTO> tickets = service.findAll(pageable);

        Assertions.assertNotNull(tickets);
        verify(ticketRepository).findAll(pageable);
    }

    @Test
    public void shouldReturnTicketWithUsersPageable() {

        Page<TicketMinDTO> tickets = service.findAllWithUsers(pageable);

        Assertions.assertNotNull(tickets);
        Assertions.assertFalse(tickets.isEmpty());
        Assertions.assertNotNull(tickets.getContent().get(0).getClient());
        verify(ticketRepository).findAllWithUsers(pageable);
    }

    @Test
    public void shouldReturnTicketByTitle() {

        Page<TicketMinDTO> tickets = service.findByTitle(pageable, expectedTitle);

        Assertions.assertNotNull(tickets);
        Assertions.assertFalse(tickets.isEmpty());
        Assertions.assertEquals(expectedTitle, tickets.getContent().get(0).getTitle());
        verify(ticketRepository).findByTitleContainingIgnoreCase(pageable, expectedTitle);
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenTitleDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findByTitle(pageable, nonExistingTitle));
        verify(ticketRepository).findByTitleContainingIgnoreCase(pageable, nonExistingTitle);
    }

    @Test
    public void shouldReturnTicketByCategory() {

        List<TicketMinDTO> tickets = service.findByCategory(expectedCategory);

        Assertions.assertNotNull(tickets);
        verify(ticketRepository).findByCategoryContainingIgnoreCase(expectedCategory);
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findByCategory(nonExistingCategory));
        verify(ticketRepository).findByCategoryContainingIgnoreCase(nonExistingCategory);
    }

    @Test
    public void shouldReturnOldestTicketFirst() {

        Page<TicketMinDTO> tickets = service.findOldestFirst(pageable);

        List<TicketMinDTO> gettingOldest = tickets.getContent();
        Assertions.assertNotNull(tickets);
        Assertions.assertFalse(tickets.isEmpty());

        for (int i = 1; i < gettingOldest.size(); i++) {
            Instant anterior = gettingOldest.get(i - 1).getCreatedAt();
            Instant atual = gettingOldest.get(i).getCreatedAt();
            Assertions.assertFalse(anterior.isAfter(atual));
        }

        verify(ticketRepository).findAllOldestFirst(pageable);
    }

    @Test
    public void shouldInsertNewTicketWhenCorrectData() {

        TicketMinDTO localDTO = service.insert(inputDTO);

        Assertions.assertNotNull(localDTO);
        Assertions.assertEquals(inputDTO.getTitle(), localDTO.getTitle());
        verify(categoryRepository).findAllById(ids);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionInInsertMethodWhenWrongCategories() {

        inputDTO.setCategories(inputDTO.getCategories().stream().map(category -> {
            CategoryDTO wrongCategory = new CategoryDTO();
            wrongCategory.setId(category.getId() + 7L);
            wrongCategory.setName(category.getName());
            return wrongCategory;
        }).collect(Collectors.toSet()));

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(inputDTO));
        verify(categoryRepository).findAllById(wrongIds);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}