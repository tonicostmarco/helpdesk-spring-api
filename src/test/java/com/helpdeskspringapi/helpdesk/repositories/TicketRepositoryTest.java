package com.helpdeskspringapi.helpdesk.repositories;

import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.factory.TicketFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class TicketRepositoryTest {

    private Long existingId;
    private Long nonExistingId;
    private Ticket ticket;
    PageImpl<Ticket> page;
    Pageable pageable;

    @Autowired
    private TicketRepository repository;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 100L;
        ticket = TicketFactory.createTicket();
        page = new PageImpl<>(List.of(ticket));

        pageable = PageRequest.of(0, 10);

    }

    @Test
    public void shouldReturnTicketWhenIdExists() {

        Optional<Ticket> ticketById = repository.findById(existingId);
        Assertions.assertNotNull(ticketById);
        Assertions.assertEquals(existingId, ticketById.get().getId());

    }

    @Test
    public void shouldReturnEmptyWhenIdDoesNotExist() {

          Optional<Ticket> ticketById = repository.findById(nonExistingId);
          Assertions.assertTrue(ticketById.isEmpty());
   }

    @Test
    public void shouldReturnTicketMinDTOWhenFindByTitleContainingIgnoreCaseExists() {

        Page<TicketMinDTO> ticketByTitle = repository.findByTitleContainingIgnoreCase(pageable, "Internet cai do nada");
        Assertions.assertNotNull(ticketByTitle);
   }

    @Test
    public void shouldReturnEmptyWhenFindByTitleContainingIgnoreCaseDoesNotExist() {

            Page<TicketMinDTO> ticketByTitle = repository.findByTitleContainingIgnoreCase(pageable, "NonExistingTitle");
            Assertions.assertTrue(ticketByTitle.isEmpty());

    }

    @Test
    public void shouldReturnTicketMinDTOWithUsers() {

        Page<TicketMinDTO> ticketWithUsers = repository.findAllWithUsers(pageable);

        Assertions.assertNotNull(ticketWithUsers);
        Assertions.assertNotNull(ticketWithUsers.map(TicketMinDTO::getClient));
    }

    @Test
    public void shouldReturnTicketMinDTOWhenFindByCategoryContainingIgnoreCaseExists() {

        List<TicketMinDTO> ticketByCategory = repository.findByCategoryContainingIgnoreCase("DNS");
        Assertions.assertNotNull(ticketByCategory);
    }

    @Test
    public void shouldReturnEmptyWhenFindByCategoryContainingIgnoreCaseDoesNotExist() {

        List<TicketMinDTO> ticketByCategory = repository.findByCategoryContainingIgnoreCase("NonExistingCategory");
        Assertions.assertTrue(ticketByCategory.isEmpty());

    }

    @Test
    public void shouldReturnAllOrderedByOldestFirst() {

        Page<TicketMinDTO> ticketOldestFirst = repository.findAllOldestFirst(pageable);


        Assertions.assertNotNull(ticketOldestFirst);
        Assertions.assertFalse(ticketOldestFirst.isEmpty());

        List<TicketMinDTO> list = ticketOldestFirst.getContent();

        for (int i = 1; i < list.size(); i++) {
            Instant anterior = list.get(i - 1).getCreatedAt();
            Instant atual = list.get(i).getCreatedAt();

            Assertions.assertFalse(atual.isBefore(anterior));
        }

    }


}
