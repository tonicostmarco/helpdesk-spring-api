package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
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

        Page<Ticket> tickets = ticketRepository.findAll(pageable);

        return tickets.map(TicketMinDTO::new);

    }

    @Transactional(readOnly = true)
    public Page<TicketMinDTO> findByTitle(Pageable pageable, String title) {

        if (title.isEmpty())

        throw new RuntimeException("Field must be filled");

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
    public TicketDTO insert(TicketDTO dto) {

        Ticket ticket = new Ticket();
        copyDtoToEntity(dto, ticket);

        Set<Long> dtos = dto.getCategories().stream().map(CategoryMinDTO::getId).collect(Collectors.toSet());

        List<Category> categories = categoryRepository.findAllById(dtos);

        if (categories.size() != dtos.size()) {
            throw new ResourceNotFoundException("There is 1 or more invalid categories");
        }

        for (CategoryMinDTO catDTO : dto.getCategories()) {

            Category cat = categoryRepository.getReferenceById(catDTO.getId());
            ticket.getCategories().add(cat);
        }

        ticket = ticketRepository.save(ticket);

        return new TicketDTO(ticket);

    }

    @Transactional
    public TicketDTO update(Long id, TicketDTO dto) {

             try {

            Ticket ticket = ticketRepository.getReferenceById(id);

            copyDtoToEntity(dto, ticket);

            ticket = ticketRepository.save(ticket);

            return new TicketDTO(ticket);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException("Ticket ID not found");
        }


    }

    @Transactional
    public void delete(Long id) {

        if (!ticketRepository.existsById(id)) {

            throw new ResourceNotFoundException("Id not found");

        }

        try { ticketRepository.deleteById(id);
        }
        catch (DatabaseException e) {
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
