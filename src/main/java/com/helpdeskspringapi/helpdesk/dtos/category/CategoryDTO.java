package com.helpdeskspringapi.helpdesk.dtos.category;

import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;

import java.util.HashSet;
import java.util.Set;

public class CategoryDTO {

    private Long id;
    private String name;

    private Set<TicketMinDTO> tickets = new HashSet<>();

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name, Set<TicketMinDTO> tickets) {
        this.id = id;
        this.name = name;
        this.tickets = tickets;
    }

    public CategoryDTO(Category category) {
        id = category.getId();
        name = category.getName();

        for (Ticket ticket : category.getCategoryTickets()) {
            tickets.add(new TicketMinDTO(ticket));
        }

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TicketMinDTO> getCategoryTickets() {
        return tickets;
    }
}
