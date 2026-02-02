package com.helpdeskspringapi.helpdesk.dtos;

import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketPriority;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class TicketDTO {

    private Long id;

    private String title;
    private String description;

    private Instant createdAt;
    private Instant updatedAt;

    private TicketPriority priority;
    private TicketStatus status;

    private User client;

    private Set<CategoryDTO> categories = new HashSet<>();

    public TicketDTO() {
    }

    public TicketDTO(Long id, String title, String description, Instant createdAt, Instant updatedAt, TicketPriority priority, TicketStatus status, User client, Set<Category> categories) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.priority = priority;
        this.status = status;
        this.client = client;
    }

    public TicketDTO(Ticket ticket) {
        id = ticket.getId();
        title = ticket.getTitle();
        description = ticket.getDescription();
        createdAt = ticket.getCreatedAt();
        updatedAt = ticket.getUpdatedAt();
        priority = ticket.getPriority();
        status = ticket.getStatus();
        client = ticket.getClient();

        for (Category cat : ticket.getCategories()) {

            categories.add(new CategoryDTO(cat));
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

}
