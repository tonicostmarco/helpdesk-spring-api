package com.helpdeskspringapi.helpdesk.dtos.ticket;

import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketPriority;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public class TicketMinDTO {

    @Schema(description = "Ticket id", example = "10")
    private Long id;

    @Schema(description = "Ticket title", example = "Internet down")
    private String title;

    @Schema(description = "Ticket client (minimal)", implementation = UserMinDTO.class)
    private UserMinDTO client;

    @Schema(description = "Ticket creation instant (UTC)", example = "2026-02-27T21:05:10Z", format = "date-time")
    private Instant createdAt;

    @Schema(description = "Ticket status", example = "OPEN")
    private TicketStatus status;

    @Schema(description = "Ticket priority", example = "MEDIUM")
    private TicketPriority priority;

    public TicketMinDTO(Long id, String title, User client, TicketStatus status, TicketPriority priority, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.client = new UserMinDTO(client);
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;

    }

    public TicketMinDTO(Ticket ticket) {
        id = ticket.getId();
        title = ticket.getTitle();
        client = new UserMinDTO(ticket.getClient());
        status = ticket.getStatus();
        priority = ticket.getPriority();
        createdAt = ticket.getCreatedAt();
    }


    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UserMinDTO getClient() {
        return client;
    }

    public TicketPriority getPriority() {
        return priority;
    }



    public void setTitle(String title) {
        this.title = title;
    }

    public void setClient(UserMinDTO client) {
        this.client = client;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }
}
