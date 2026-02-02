package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.*;
import com.helpdeskspringapi.helpdesk.dtos.TicketDTO;
import com.helpdeskspringapi.helpdesk.entities.*;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.repositories.CategoryRepository;
import com.helpdeskspringapi.helpdesk.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public TicketDTO findById(Long id) {

       Ticket ticket = ticketRepository.findById(id).orElseThrow();

        return new TicketDTO(ticket);

    }

    @Transactional(readOnly = true)
    public Page<TicketDTO> findAll(Pageable pageable) {

        Page<Ticket> tickets = ticketRepository.findAll(pageable);

        return tickets.map(TicketDTO::new);

    }

    @Transactional
    public TicketDTO insert(TicketDTO dto) {

        Ticket ticket = new Ticket();
        copyDtoToEntity(dto, ticket);

        for (CategoryDTO catDTO : dto.getCategories()) {

            Category cat = categoryRepository.getReferenceById(catDTO.getId());
            ticket.getCategories().add(cat);
        }
        ticket = ticketRepository.save(ticket);

        return new TicketDTO(ticket);

    }

    @Transactional
    public TicketDTO update(Long id, TicketDTO dto) {

        Ticket ticket = ticketRepository.getReferenceById(id);

        copyDtoToEntity(dto, ticket);

        ticket = ticketRepository.save(ticket);

        return new TicketDTO(ticket);

    }

    @Transactional
    public void delete(Long id) {
        ticketRepository.deleteById(id);
    }

    private void copyDtoToEntity(TicketDTO dto, Ticket ticket) {
        ticket.setId(dto.getId());
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setCreatedAt(dto.getCreatedAt());
        ticket.setUpdatedAt(dto.getUpdatedAt());
        ticket.setPriority(dto.getPriority());
        ticket.setStatus(dto.getStatus());
        ticket.setClient(dto.getClient());
    }

}
