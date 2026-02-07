package com.helpdeskspringapi.helpdesk.dtos;

import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.entities.User;

import java.time.Instant;

public class TicketMinDTO {

    private Long id;
    private String title;
    private UserMinDTO client;
    private Instant createdAt;

    public TicketMinDTO(Long id, String title, User client, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.client = new UserMinDTO(client);
        this.createdAt = createdAt;

    }

    public TicketMinDTO(Ticket ticket) {
        id = ticket.getId();
        title = ticket.getTitle();
        client = new UserMinDTO(ticket.getClient());
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
