package com.helpdeskspringapi.helpdesk.dtos;

import com.helpdeskspringapi.helpdesk.entities.Ticket;

public class TicketMinDTO {

    private String title;

    public TicketMinDTO(String title) {
        this.title = title;
    }

    public TicketMinDTO(Ticket ticket) {
        title = ticket.getTitle();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
