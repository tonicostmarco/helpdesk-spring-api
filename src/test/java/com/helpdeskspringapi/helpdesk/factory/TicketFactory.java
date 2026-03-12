package com.helpdeskspringapi.helpdesk.factory;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketPatchDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketPriority;
import com.helpdeskspringapi.helpdesk.entities.enums.TicketStatus;

import java.time.Instant;
import java.util.Set;

public class TicketFactory {

    public static Ticket createTicket() {

        Ticket ticket = new Ticket(
                "Internet cai do nada",
                "Cai a cada 10 minutos. Modem reinicia sozinho.",
                Instant.parse("2026-02-18T10:10:00Z"),
                Instant.parse("2026-02-18T10:10:00Z"),
                TicketPriority.MEDIUM,
                TicketStatus.OPEN,
                new User(
                        "Marco Admin",
                        19,
                        "991731543",
                        "admin@helpdesk.com",
                        "$2b$10$8orZfrgp/uRwNstcqzYmI.jtGSlcpLEugS0xk1wefRW2KUOkEuuf2",
                        Set.of(new Role(1L, "ROLE_ADMIN"))
                )
        );
        ticket.setId(1L);

        Category category = CategoryFactory.createCategory();

        ticket.getCategories().add(category);

        return ticket;
    }

    public static TicketDTO createTicketDTO() {

        return new TicketDTO(createTicket());

    }

    public static TicketMinDTO createTicketMinDTO() {

        TicketMinDTO minDTO = new TicketMinDTO(createTicket());

        return minDTO;

    }

    public static TicketInputDTO createTicketInputDTO() {

        TicketInputDTO dto = new TicketInputDTO();

        dto.setId(1L);
        dto.setTitle("Internet cai do nada");
        dto.setDescription("Cai a cada 10 minutos. Modem reinicia sozinho.");

        CategoryDTO catDTO = CategoryFactory.createCategoryDTO();

        dto.getCategories().add(catDTO);

        return dto;
    }

    public static TicketPatchDTO createTicketPatchDTO() {

        TicketPatchDTO dto = new TicketPatchDTO();

        dto.setPriority(TicketPriority.LOW);
        dto.setStatus(TicketStatus.CLOSED);

        return dto;
    }

}
