package com.helpdeskspringapi.helpdesk.repositories;

import com.helpdeskspringapi.helpdesk.dtos.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;


public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT new com.helpdeskspringapi.helpdesk.dtos.TicketMinDTO(obj.id, obj.title, obj.client, obj.createdAt) " +
            "FROM Ticket obj " +
            "WHERE UPPER(obj.title) LIKE UPPER(CONCAT('%', :title, '%'))")
    Page<TicketMinDTO> findByTitleContainingIgnoreCase(Pageable pageable, @Param("title") String title);

    @Query("SELECT new com.helpdeskspringapi.helpdesk.dtos.TicketMinDTO(obj.id, obj.title, obj.client, obj.createdAt) " +
            "FROM Ticket obj " +
            "JOIN obj.categories cat " +
            "WHERE UPPER(cat.name) = UPPER(:category)")
    Page<TicketMinDTO> findByCategoryContainingIgnoreCase(Pageable pageable, String category);

    @Query("SELECT new com.helpdeskspringapi.helpdesk.dtos.TicketMinDTO(obj.id, obj.title, obj.client, obj.createdAt) " +
            "FROM Ticket obj " +
            "ORDER BY obj.createdAt ASC")
    Page<TicketMinDTO> findAllOldestFirst(Pageable pageable);

    @Query("SELECT new com.helpdeskspringapi.helpdesk.dtos.TicketMinDTO(obj.id, obj.title, obj.client, obj.createdAt) " +
            "FROM Ticket obj " +
            "WHERE UPPER(obj.client.name) = UPPER(:name)")
    Page<TicketMinDTO> findByClientName(Pageable pageable, @Param("name") String name);

}
