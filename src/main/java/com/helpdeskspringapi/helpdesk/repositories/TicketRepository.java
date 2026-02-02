package com.helpdeskspringapi.helpdesk.repositories;

import com.helpdeskspringapi.helpdesk.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
