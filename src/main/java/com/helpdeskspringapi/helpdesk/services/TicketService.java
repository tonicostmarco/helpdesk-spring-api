package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketPatchDTO;
import com.helpdeskspringapi.helpdesk.dtos.twillio.MessageRequest;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.exceptions.*;
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

import static com.helpdeskspringapi.helpdesk.entities.enums.TicketPriority.LOW;
import static com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus.OPEN;



@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private MessageSender messageSender;

    @Transactional(readOnly = true)
    public TicketMinDTO findById(Long id) {

        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        authService.selfOrAdmin(ticket.getClient().getId());

        return new TicketMinDTO(ticket);

    }

    @Transactional(readOnly = true)
    public Page<TicketMinDTO> findAll(Pageable pageable) {

        try {
            Page<Ticket> ticket = ticketRepository.findAll(pageable);
            return ticket.map(TicketMinDTO::new);
        }
        catch (ForbiddenException e) {
            throw new ForbiddenException("Access denied");
        }

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
    public TicketMinDTO insert(TicketInputDTO dto) {

        User me = userAuthService.authenticated();

        Set<Long> dtos = dto.getCategories().stream().map(CategoryDTO::getId).collect(Collectors.toSet());

        List<Category> categories = categoryRepository.findAllById(dtos);

        if (categories.size() != dtos.size()) {
            throw new ResourceNotFoundException("There is 1 or more invalid categories");
        }

        Ticket ticket = new Ticket();
        ticket.getCategories().clear();
        ticket.getCategories().addAll(categories);



        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(LOW);
        ticket.setStatus(OPEN);
        ticket.setCreatedAt(Instant.now());
        ticket.setUpdatedAt(Instant.EPOCH);
        ticket.setClient(me);

        ticket = ticketRepository.save(ticket);

        return new TicketMinDTO(ticket);

    }

    @Transactional
    public TicketMinDTO update(Long id, TicketDTO dto) {
        try {

            Ticket ticket = ticketRepository.getReferenceById(id);
            ticket.setPriority(dto.getPriority());
            ticket.setStatus(dto.getStatus());
            ticket.setUpdatedAt(Instant.now());

            ticket = ticketRepository.save(ticket);
            return new TicketMinDTO(ticket);

        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Ticket ID not found");
        }

    }


    @Transactional
    public TicketMinDTO patch(Long id, TicketPatchDTO dto) {

        try {
            Ticket ticket = ticketRepository.getReferenceById(id);

            if (dto.getStatus() != null && dto.getStatus() != ticket.getStatus()) {
                ticket.setUpdatedAt(Instant.now());
                ticket.setStatus(dto.getStatus());
            } else {
                throw new BusinessException("Wasn't able to change");
            }


            ticket = ticketRepository.save(ticket);

            messageSender.sendSms(
                    new MessageRequest(userAuthService.getMe().getName(),
                            ticketRepository.getReferenceById(id).getClient().getDdd(), ticketRepository.getReferenceById(id).getClient().getPhone(),
                            "Your ticket has been updated. "), "Status: " + dto.getStatus());


            return new TicketMinDTO(ticket);
        }
        catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Ticket ID not found");
        }
        catch (RuntimeException e) {
            throw new MessageException("Wasn't able to deliver the message");
        }
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

}
