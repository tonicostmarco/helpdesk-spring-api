package com.helpdeskspringapi.helpdesk.dtos.ticket;

import com.helpdeskspringapi.helpdesk.entities.enums.TicketPriority;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public class TicketPatchDTO {

    @Schema(description = "New ticket status", example = "IN_PROGRESS")
    private TicketStatus status;

    @Schema(description = "New ticket priority", example = "HIGH")
    private TicketPriority priority;

    public TicketPatchDTO() {

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
