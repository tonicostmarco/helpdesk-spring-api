package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketPatchDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.exceptions.BusinessException;
import com.helpdeskspringapi.helpdesk.exceptions.DatabaseException;
import com.helpdeskspringapi.helpdesk.exceptions.InvalidParameterException;
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.repositories.CategoryRepository;
import com.helpdeskspringapi.helpdesk.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public TicketMinDTO findById(Long id) {

        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return new TicketMinDTO(ticket);

    }

    @Transactional(readOnly = true)
    public Page<TicketMinDTO> findAll(Pageable pageable) {
        Page<Ticket> ticket = ticketRepository.findAll(pageable);
        return ticket.map(TicketMinDTO::new);

    }

    @Transactional(readOnly = true)
    public Page<TicketMinDTO> findAllWithUsers(Pageable pageable) {

        return ticketRepository.findAllWithUsers(pageable);

    }


    @Transactional(readOnly = true)
    public Page<TicketMinDTO> findByTitle(Pageable pageable, String title) {

        if (title.isBlank()) {
            throw new InvalidParameterException("Title required");
        }
        try {

            return ticketRepository.findByTitleContainingIgnoreCase(pageable, title);

        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Ticket title not found");
        }
    }

    @Transactional(readOnly = true)
    public List<TicketMinDTO> findByCategory(String category) {

        if (category.isBlank()) {
            throw new InvalidParameterException("Category required");
        }

        try {
            return ticketRepository.findByCategoryContainingIgnoreCase(category);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Ticket category not found");
        }
    }

    @Transactional(readOnly = true)
    public Page<TicketMinDTO> findOldestFirst(Pageable pageable) {

        return ticketRepository.findAllOldestFirst(pageable);

    }

    @Transactional
    public TicketMinDTO insert(TicketDTO dto) {


        Set<Long> dtos = dto.getCategories().stream().map(CategoryMinDTO::getId).collect(Collectors.toSet());

        List<Category> categories = categoryRepository.findAllById(dtos);

        if (categories.size() != dtos.size()) {
            throw new ResourceNotFoundException("There is 1 or more invalid categories");
        }

        Ticket ticket = new Ticket();
        copyDtoToEntity(dto, ticket);

        ticket.getCategories().addAll(categories);
        ticket = ticketRepository.save(ticket);

        return new TicketMinDTO(ticket);

    }

    @Transactional
    public TicketMinDTO update(Long id, TicketDTO dto) {
        try {

            Ticket ticket = ticketRepository.getReferenceById(id);
            copyDtoToEntity(dto, ticket);

            ticket = ticketRepository.save(ticket);

            return new TicketMinDTO(ticket);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Ticket ID not found");
        }

    }


    @Transactional
    public TicketMinDTO patch(Long id, TicketPatchDTO dto) {

        Ticket ticket = ticketRepository.getReferenceById(id);

       if (dto.getStatus() != null && dto.getStatus() != ticket.getStatus()) {
           ticket.setUpdatedAt(Instant.now());
           ticket.setStatus(dto.getStatus());
       }
       else {
           throw new BusinessException("Wasn't able to change");
       }

        ticket = ticketRepository.save(ticket);

        return new TicketMinDTO(ticket);
    }


    @Transactional
    public void delete(Long id) {

        if (!ticketRepository.existsById(id)) {

            throw new ResourceNotFoundException("Id not found");

        }

        try {
            ticketRepository.deleteById(id);
        } catch (DatabaseException e) {
            throw new DatabaseException("Referential integrity failure");
        }

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
