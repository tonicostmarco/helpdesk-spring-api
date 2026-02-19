package com.helpdeskspringapi.helpdesk.dtos.ticket;

import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus;

import java.time.Instant;

public class TicketMinDTO {

    private Long id;
    private String title;
    private UserMinDTO client;
    private Instant createdAt;
    private TicketStatus status;


    public TicketMinDTO(Long id, String title, User client, TicketStatus status, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.client = new UserMinDTO(client);
        this.status = status;
        this.createdAt = createdAt;

    }

    public TicketMinDTO(Ticket ticket) {
        id = ticket.getId();
        title = ticket.getTitle();
        client = new UserMinDTO(ticket.getClient());
        status = ticket.getStatus();
        createdAt = ticket.getCreatedAt();
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserMinDTO getClient() {
        return client;
    }

    public void setClient(UserMinDTO client) {
        this.client = client;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
