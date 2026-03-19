package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketPatchDTO;
import com.helpdeskspringapi.helpdesk.exceptions.InvalidParameterException;
import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketPriority;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus;
import com.helpdeskspringapi.helpdesk.exceptions.BusinessException;
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.exceptions.DatabaseException;
import com.helpdeskspringapi.helpdesk.factory.CategoryFactory;
import com.helpdeskspringapi.helpdesk.factory.TicketFactory;
import com.helpdeskspringapi.helpdesk.repositories.CategoryRepository;
import com.helpdeskspringapi.helpdesk.repositories.TicketRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TicketServiceTest {


    private Long existingId;
    private Long nonExistingId;

    private Ticket ticket;
    private TicketDTO ticketDTO;
    private TicketMinDTO minDTO;
    private TicketInputDTO inputDTO;
    private TicketPatchDTO patchDTO;
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

    private TicketService service;

    @BeforeEach
    void setUp() {

        service = new TicketService(
                ticketRepository,
                categoryRepository,
                authService,
                userAuthService,
                messageSender
        );

        existingId = 1L;
        nonExistingId = 100L;

        ticket = TicketFactory.createTicket();
        ticketDTO = TicketFactory.createTicketDTO();
        minDTO = TicketFactory.createTicketMinDTO();
        inputDTO = TicketFactory.createTicketInputDTO();
        patchDTO = TicketFactory.createTicketPatchDTO();

        listMinDTO = List.of(minDTO);

        Category cat = CategoryFactory.createCategory();
        categories = List.of(cat);

        page = new PageImpl<>(List.of(ticket));
        pageMin = new PageImpl<>(List.of(minDTO));
        pageable = PageRequest.of(0, 10);

        when(ticketRepository.findById(existingId)).thenReturn(Optional.of(ticket));
        when(ticketRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(ticketRepository.findAll(pageable)).thenReturn(page);
        when(ticketRepository.findAllWithUsers(pageable)).thenReturn(pageMin);
        when(ticketRepository.findAllOldestFirst(pageable)).thenReturn(pageMin);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ticketRepository.getReferenceById(existingId)).thenReturn(ticket);

        when(ticketRepository.getReferenceById(nonExistingId)).thenThrow(new jakarta.persistence.EntityNotFoundException());

        User authenticatedUser = ticket.getClient();
        when(userAuthService.authenticated()).thenReturn(authenticatedUser);
        when(userAuthService.getMe()).thenReturn(new UserDTO(ticket.getClient()));

        doNothing().when(ticketRepository).deleteById(existingId);
        when(ticketRepository.existsById(existingId)).thenReturn(true);
        when(ticketRepository.existsById(nonExistingId)).thenReturn(false);

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

        String expectedTitle = "Internet cai do nada";
        when(ticketRepository.findByTitleContainingIgnoreCase(expectedTitle)).thenReturn(listMinDTO);

        List<TicketMinDTO> tickets = service.findByTitle(expectedTitle);

        Assertions.assertNotNull(tickets);
        Assertions.assertFalse(tickets.isEmpty());
        Assertions.assertEquals(expectedTitle, tickets.getFirst().getTitle());

        verify(ticketRepository).findByTitleContainingIgnoreCase(expectedTitle);
    }

    @Test
    public void shouldThrowInvalidParameterExceptionWhenTitleIsBlank() {

        Assertions.assertThrows(InvalidParameterException.class, () -> service.findByTitle("   "));
        verify(ticketRepository, never()).findByTitleContainingIgnoreCase(any());

    }


    @Test
    public void shouldThrowInvalidParameterExceptionWhenCategoryIsBlank() {

        Assertions.assertThrows(InvalidParameterException.class, () -> service.findByCategory("  "));
        verify(ticketRepository, never()).findByCategory(any());
        verify(ticketRepository, never()).existsByCategoriesName(any());

    }

    @Test
    public void shouldReturnTicketWhenFindMe() {

        TicketMinDTO result = service.findMe(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        verify(ticketRepository).findById(existingId);

    }

    @Test
    public void shouldCopyDTOtoEntitySettingFields() {

        User me = ticket.getClient();
        when(userAuthService.authenticated()).thenReturn(me);

        Ticket newTicket = new Ticket();
        service.copyDTOtoEntity(inputDTO, newTicket);

        Assertions.assertNotNull(newTicket);
        Assertions.assertEquals(inputDTO.getTitle(), newTicket.getTitle());
        Assertions.assertEquals(inputDTO.getDescription(), newTicket.getDescription());
        Assertions.assertNotNull(newTicket.getCreatedAt());
        Assertions.assertNotNull(newTicket.getUpdatedAt());
        Assertions.assertEquals(me, newTicket.getClient());

    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenTitleDoesNotExist() {
        String nonExistingTitle = "nonExistingTitle";
        when(ticketRepository.findByTitleContainingIgnoreCase(nonExistingTitle)).thenThrow(ResourceNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findByTitle(nonExistingTitle));
        verify(ticketRepository).findByTitleContainingIgnoreCase(nonExistingTitle);
    }

    @Test
    public void shouldReturnTicketByCategory() {
        String expectedCategory = "DNS";
        when(ticketRepository.findByCategory(expectedCategory)).thenReturn(listMinDTO);
        when(ticketRepository.existsByCategoriesName(expectedCategory)).thenReturn(true);

        List<TicketMinDTO> tickets = service.findByCategory(expectedCategory);

        Assertions.assertNotNull(tickets);
        verify(ticketRepository).findByCategory(expectedCategory);
        verify(ticketRepository).existsByCategoriesName(expectedCategory);
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist() {
        String nonExistingCategory = "nonExistingCategory";
        doThrow(ResourceNotFoundException.class).when(ticketRepository).findByCategory(nonExistingCategory);
        when(ticketRepository.existsByCategoriesName(nonExistingCategory)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findByCategory(nonExistingCategory));

        verify(ticketRepository, never()).findByCategory(nonExistingCategory);
        verify(ticketRepository).existsByCategoriesName(nonExistingCategory);
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

        Set<Long> ids = inputDTO.getCategories().stream().map(CategoryDTO::getId).collect(Collectors.toSet());
        when(categoryRepository.findAllById(ids)).thenReturn(categories);

        TicketMinDTO localDTO = service.insert(inputDTO);

        Assertions.assertNotNull(localDTO);
        Assertions.assertEquals(inputDTO.getTitle(), localDTO.getTitle());
        verify(categoryRepository).findAllById(ids);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionInInsertMethodWhenWrongCategories() {

        Set<Long> wrongIds = inputDTO.getCategories().stream().map(x -> x.getId() + 7L).collect(Collectors.toSet());
        when(categoryRepository.findAllById(wrongIds)).thenReturn(List.of());

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

    @Test
    public void shouldUpdateTicketWhenCorrectData() {

        TicketMinDTO localDTO = service.update(existingId, patchDTO);

        Assertions.assertNotNull(localDTO);
        Assertions.assertEquals(ticketDTO.getTitle(), localDTO.getTitle());
        verify(ticketRepository).getReferenceById(existingId);
        verify(ticketRepository).save(any(Ticket.class));

    }

    @Test
    public void shouldThrowResourceNotFoundExceptionInUpdateMethodWhenWrongId() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, patchDTO));
        verify(ticketRepository).getReferenceById(nonExistingId);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    public void shouldUpdateStatusWhenCorrectIdAndData() {

        TicketMinDTO localDTO = service.patchStatus(existingId, patchDTO);

        verify(ticketRepository).getReferenceById(existingId);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    public void shouldThrowBusinessExceptionInPatchStatusWhenInvalidStatus() {

        patchDTO.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        Assertions.assertThrows(BusinessException.class, () -> service.patchStatus(existingId, patchDTO));
        verify(ticketRepository).getReferenceById(existingId);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionInPatchStatusWhenIdDoesNotExist() {


        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.patchStatus(nonExistingId, patchDTO));
        verify(ticketRepository).getReferenceById(nonExistingId);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    public void shouldThrowBusinessExceptionInPatchPriorityWhenInvalidStatus() {

        patchDTO.setPriority(TicketPriority.LOW);
        ticket.setPriority(TicketPriority.LOW);

        Assertions.assertThrows(BusinessException.class, () -> service.patchPriority(existingId, patchDTO));
        verify(ticketRepository).getReferenceById(existingId);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    public void shouldThrowresourceNotFoundExceptionInPatchPriorityWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.patchPriority(nonExistingId, patchDTO));
        verify(ticketRepository).getReferenceById(nonExistingId);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    public void shouldDeleteTicketWhenIdExistsAndTicketIsClosed() {
        ticket.setStatus(TicketStatus.CLOSED);
        service.delete(existingId);
        verify(ticketRepository).deleteById(existingId);
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
        verify(ticketRepository, never()).deleteById(nonExistingId);

    }

    @Test
    public void shouldThrowBusinessExceptionWhenStatusIsNotClosed() {

        Assertions.assertThrows(BusinessException.class, () -> service.delete(existingId));
        verify(ticketRepository, never()).deleteById(existingId);

    }

    @Test
    public void shouldThrowDataIntegrityExceptionWhenDependentId() {
        ticket.setStatus(TicketStatus.CLOSED);

        doThrow(new DatabaseException("Referential integrity failure")).when(ticketRepository).deleteById(existingId);

        Assertions.assertThrows(DatabaseException.class, () -> service.delete(existingId));
        verify(ticketRepository).deleteById(existingId);

    }

}