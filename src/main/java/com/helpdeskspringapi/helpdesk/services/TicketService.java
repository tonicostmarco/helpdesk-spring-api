package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.helpdeskspringapi.helpdesk.entities.enums.TicketPriority.LOW;
import static com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus.CLOSED;
import static com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus.OPEN;
import static java.time.Instant.EPOCH;
import static java.time.Instant.now;


@Service
public class TicketService {


    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final AuthService authService;
    private final UserAuthService userAuthService;
    private final MessageSender messageSender;

    public TicketService(TicketRepository ticketRepository,
                         CategoryRepository categoryRepository,
                         AuthService authService,
                         UserAuthService userAuthService,
                         MessageSender messageSender) {

        this.ticketRepository = ticketRepository;
        this.categoryRepository = categoryRepository;
        this.authService = authService;
        this.userAuthService = userAuthService;
        this.messageSender = messageSender;
    }

    @Transactional(readOnly = true)
    public TicketMinDTO findById(Long id) {

        try {
            Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
            authService.selfOrAllowed(ticket.getClient().getId());
            return new TicketMinDTO(ticket);
        } catch (ForbiddenException e) {
            throw new ForbiddenException("Access denied");
        }


    }

    @Transactional(readOnly = true)
    public TicketMinDTO findMe(Long id) {
        try {
            Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
            authService.selfOrAllowed(ticket.getClient().getId());

            return new TicketMinDTO(ticket);
        } catch (ForbiddenException e) {
            throw new ForbiddenException("Access denied");
        }
    }

    @Transactional(readOnly = true)
    public Page<TicketMinDTO> findAll(Pageable pageable) {
        try {

            Page<Ticket> ticket = ticketRepository.findAll(pageable);
            return ticket.map(TicketMinDTO::new);

        } catch (ForbiddenException e) {
            throw new ForbiddenException("Access denied");
        }
    }

    @Transactional(readOnly = true)
    public Page<TicketMinDTO> findAllWithUsers(Pageable pageable) {
        try {
            return ticketRepository.findAllWithUsers(pageable);
        } catch (ForbiddenException e) {
            throw new ForbiddenException("Access denied");
        }
    }


    @Transactional(readOnly = true)
    public List<TicketMinDTO> findByTitle(String title) {

        if (title.isBlank()) {
            throw new InvalidParameterException("Title required");
        }

        try {

            List<TicketMinDTO> result = ticketRepository.findByTitleContainingIgnoreCase(title);

            if (result.isEmpty()) {
                throw new ResourceNotFoundException("Ticket title not found");
            }

            return result;

        } catch (ForbiddenException e) {
            throw new ForbiddenException("Access denied");
        }
    }

    @Transactional(readOnly = true)
    public List<TicketMinDTO> findByCategory(String category) {

        if (category.isBlank()) {
            throw new InvalidParameterException("Category required");
        }
        if (!ticketRepository.existsByCategoriesName(category)) {
            throw new ResourceNotFoundException("Ticket category not found");
        }
        try {
            return ticketRepository.findByCategory(category);
        } catch (ForbiddenException e) {
            throw new ForbiddenException("Access denied");
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

        copyDTOtoEntity(dto, ticket);

        ticket = ticketRepository.save(ticket);
        sendTicketSms(
                me.getName(),
                me.getDdd(),
                me.getPhone(),
                "Your ticket has been created. Status: " + ticket.getStatus()
        );

        return new TicketMinDTO(ticket);

    }

    @Transactional
    public TicketMinDTO update(Long id, TicketPatchDTO dto) {
        try {

            Ticket ticket = ticketRepository.getReferenceById(id);
            ticket.setPriority(dto.getPriority());
            ticket.setStatus(dto.getStatus());
            ticket.setUpdatedAt(Instant.now());

            ticket = ticketRepository.save(ticket);
            sendTicketSms(
                    userAuthService.getMe().getName(),
                    ticket.getClient().getDdd(),
                    ticket.getClient().getPhone(),
                    "Your ticket status has been updated. Status: " + dto.getStatus()
            );

            return new TicketMinDTO(ticket);

        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Ticket ID not found");
        }

    }


    @Transactional
    public TicketMinDTO patchStatus(Long id, TicketPatchDTO dto) {
        try {
            Ticket ticket = ticketRepository.getReferenceById(id);

            if (dto.getStatus() != null && dto.getStatus() != ticket.getStatus()) {
                ticket.setUpdatedAt(Instant.now());
                ticket.setStatus(dto.getStatus());
            } else {
                throw new BusinessException("Wasn't able to change");
            }

            ticket = ticketRepository.save(ticket);
            sendTicketSms(
                    userAuthService.getMe().getName(),
                    ticket.getClient().getDdd(),
                    ticket.getClient().getPhone(),
                    "Your ticket status has been updated. Status: " + dto.getStatus()
            );

            return new TicketMinDTO(ticket);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Ticket ID not found");
        }
    }

    @Transactional
    public TicketMinDTO patchPriority(Long id, TicketPatchDTO dto) {

        try {
            Ticket ticket = ticketRepository.getReferenceById(id);

            if (dto.getPriority() != null && dto.getPriority() != ticket.getPriority()) {
                ticket.setUpdatedAt(Instant.now());
                ticket.setPriority(dto.getPriority());
            } else {
                throw new BusinessException("Wasn't able to change");
            }

            ticket = ticketRepository.save(ticket);

            sendTicketSms(
                    userAuthService.getMe().getName(),
                    ticket.getClient().getDdd(),
                    ticket.getClient().getPhone(),
                    "Your ticket has been updated. Priority: " + dto.getPriority()
            );

            return new TicketMinDTO(ticket);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Ticket ID not found");
        }

    }


    @Transactional
    public void delete(Long id) {

        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Id not found");
        }
        if (!ticketRepository.getReferenceById(id).getStatus().equals(CLOSED)) {
            throw new BusinessException("You can't delete an opened ticket");
        }

        try {
            ticketRepository.deleteById(id);

        } catch (DatabaseException e) {
            throw new DatabaseException("Referential integrity failure");
        }

    }

    public void copyDTOtoEntity(TicketInputDTO dto, Ticket ticket) {
        User me = userAuthService.authenticated();
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setPriority(LOW);
        ticket.setStatus(OPEN);
        ticket.setCreatedAt(now());
        ticket.setUpdatedAt(EPOCH);
        ticket.setClient(me);

    }

    private void sendTicketSms(String senderName, Integer ddd, String phone, String message) {
        try {
            messageSender.sendSms(new MessageRequest(senderName, ddd, phone, message));
        } catch (Exception e) {
            throw new MessageException("Error on sending message: " + e.getMessage());
        }
    }

}
