package com.helpdeskspringapi.helpdesk.dtos.ticket;

import com.helpdeskspringapi.helpdesk.entities.enums.TicketPriority;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus;

public class TicketPatchDTO {

    private TicketStatus status;
    private TicketPriority priority;

    public TicketPatchDTO(TicketPriority priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public TicketPriority getPriority() {
        return priority;
    }


    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }
}
