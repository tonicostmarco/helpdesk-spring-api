package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TicketServiceTest {

    @InjectMocks
    private TicketService service;

    private Long existingId;
    private Long nonExistingId;
    Set<Long> ids;
    Set<Long> wrongIds;
    private String expectedTitle;
    private String nonExistingTitle;
    private String expectedCategory;
    private String nonExistingCategory;

    private Ticket ticket;
    private TicketDTO dto;
    private TicketMinDTO minDTO;
    private List<TicketMinDTO> listMinDTO = new ArrayList<>();
    private TicketInputDTO inputDTO;
    private Category cat;
    private CategoryDTO categoryDTO;
    List<Category> categories = new ArrayList<>();

    PageImpl<Ticket> page;
    PageImpl<TicketMinDTO> pageMin;
    Pageable pageable;

    @Mock
    private TicketRepository repository;

    @Mock
    private CategoryRepository catRepository;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;

        nonExistingId = 100L;
        expectedTitle = "Internet cai do nada";
        nonExistingTitle = "nonExistingTitle";
        expectedCategory = "DNS";
        nonExistingCategory = "nonExistingCategory";

        ticket = TicketFactory.createTicket();
        dto = TicketFactory.createTicketDTO();
        minDTO = TicketFactory.createTicketMinDTO();
        inputDTO = TicketFactory.createTicketInputDTO();
        listMinDTO.add(minDTO);

        cat = CategoryFactory.createCategory();
        categoryDTO = CategoryFactory.createCategoryDTO();

        page = new PageImpl<>(List.of(ticket));
        pageMin = new PageImpl<>(List.of(minDTO));
        pageable = PageRequest.of(0, 10);

        ids = inputDTO.getCategories().stream().map(CategoryDTO::getId).collect(Collectors.toSet());

        wrongIds = inputDTO.getCategories().stream().map(x -> x.getId() + 7L).collect(Collectors.toSet());

        when(repository.findById(existingId)).thenReturn(Optional.of(ticket));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(repository.findByTitleContainingIgnoreCase(pageable, expectedTitle)).thenReturn(pageMin);
        doThrow(ResourceNotFoundException.class).when(repository).findByTitleContainingIgnoreCase(pageable, nonExistingTitle);

        when(repository.findByCategoryContainingIgnoreCase(expectedCategory)).thenReturn(listMinDTO);
        doThrow(ResourceNotFoundException.class).when(repository).findByCategoryContainingIgnoreCase(nonExistingCategory);

        when(catRepository.findAllById(ids)).thenReturn(categories);
        doThrow(ResourceNotFoundException.class).when(catRepository).findAllById(wrongIds);

        when(repository.findAll(pageable)).thenReturn(page);
        when(repository.findAllWithUsers(pageable)).thenReturn(pageMin);
        when(repository.findAllOldestFirst(pageable)).thenReturn(pageMin);

    }

    @Test
    public void shouldReturnTicketMinDTOWhenIdExists() {

        minDTO = service.findById(existingId);

        Assertions.assertNotNull(minDTO);
        Assertions.assertEquals(existingId, minDTO.getId());
        verify(repository).findById(existingId);

    }

    @Test
    public void shouldThrowResourceNotFoundExceptionTicketWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
        verify(repository).findById(nonExistingId);
    }

    @Test
    public void shouldReturnTicketPageable() {

        Page<TicketMinDTO> tickets = service.findAll(pageable);

        Assertions.assertNotNull(tickets);
        verify(repository).findAll(pageable);

    }

    @Test
    public void shouldReturnTicketWithUsersPageable() {

        Page<TicketMinDTO> tickets = service.findAllWithUsers(pageable);

        Assertions.assertNotNull(tickets);
        Assertions.assertNotNull(tickets.get().map(TicketMinDTO::getClient));
        verify(repository).findAllWithUsers(pageable);

    }

    @Test
    public void shouldReturnTicketByTitle() {

        Page<TicketMinDTO> tickets = service.findByTitle(pageable, expectedTitle);

        Assertions.assertNotNull(tickets);
        Assertions.assertNotNull(tickets.get().map(TicketMinDTO::getTitle));
        Assertions.assertEquals(expectedTitle, minDTO.getTitle());
        verify(repository).findByTitleContainingIgnoreCase(pageable, expectedTitle);

    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenTitleDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findByTitle(pageable, nonExistingTitle);
            verify(repository).findByTitleContainingIgnoreCase(pageable, expectedTitle);
        });
    }

    @Test
    public void shouldReturnTicketByCategory() {

        List<TicketMinDTO> tickets = service.findByCategory(expectedCategory);

        Assertions.assertNotNull(tickets);
        verify(repository).findByCategoryContainingIgnoreCase(expectedCategory);

    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findByCategory(nonExistingCategory);
            verify(repository).findByCategoryContainingIgnoreCase(expectedCategory);
        });
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

            Assertions.assertTrue(anterior.isAfter(atual));

        }

        verify(repository).findAllOldestFirst(pageable);

    }

    @Test
    public void shouldInsertNewTicketWhenCorrectData()  {

        categoryDTO.setId(1L);
        TicketMinDTO localDTO = service.insert(inputDTO);
        Assertions.assertNotNull(localDTO);

        verify(service).insert(inputDTO);

    }

    @Test
    public void shouldThrowResourceNotFoundExceptionInInsertMethodWhenWrongCategories()  {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.insert(inputDTO);
            verify(service).insert(inputDTO);
        });


    }
}
