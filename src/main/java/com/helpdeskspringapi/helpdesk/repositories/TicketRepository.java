package com.helpdeskspringapi.helpdesk.repositories;

import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT new com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO(obj.id, obj.title, obj.client, obj.status, obj.priority, obj.createdAt) " +
            "FROM Ticket obj " +
            "WHERE UPPER(obj.title) LIKE UPPER(CONCAT('%', :title, '%'))")
    Page<TicketMinDTO> findByTitleContainingIgnoreCase(Pageable pageable, @Param("title") String title);

    @Query(value = "SELECT new com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO(obj.id, obj.title, obj.client, obj.status, obj.priority, obj.createdAt) " +
            "FROM Ticket obj " +
            "JOIN obj.client ",
            countQuery = "SELECT COUNT(obj) " +
                    "FROM Ticket obj " +
                    "JOIN obj.client")
    Page<TicketMinDTO> findAllWithUsers(Pageable pageable);

    @Query("SELECT new com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO(obj.id, obj.title, obj.client, obj.status, obj.priority, obj.createdAt) " +
            "FROM Ticket obj " +
            "JOIN obj.categories cat " +
            "WHERE UPPER(cat.name) = UPPER(:category)")
    List<TicketMinDTO> findByCategoryContainingIgnoreCase(@Param("category") String category);

    @Query("SELECT new com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO(obj.id, obj.title, obj.client, obj.status, obj.priority, obj.createdAt) " +
            "FROM Ticket obj " +
            "ORDER BY obj.createdAt ASC")
    Page<TicketMinDTO> findAllOldestFirst(Pageable pageable);

}
