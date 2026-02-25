package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketPatchDTO;
import com.helpdeskspringapi.helpdesk.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/tickets")
public class TicketController {

    @Autowired
    private TicketService service;

    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<TicketMinDTO> findById(@PathVariable Long id) {

        TicketMinDTO dto = service.findById(id);

        return ResponseEntity.ok(dto);

    }

    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<TicketMinDTO>> findAll(@Valid Pageable pageable) {

        Page<TicketMinDTO> dto = service.findAll(pageable);

        return ResponseEntity.ok(dto);

    }

    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN')")
    @GetMapping("/searchusers")
    public ResponseEntity<Page<TicketMinDTO>> findAllWithUsers(Pageable pageable) {

        Page<TicketMinDTO> dto = service.findAllWithUsers(pageable);

        return ResponseEntity.ok(dto);

    }

    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN')")
    @GetMapping("/searchtitle")
    public ResponseEntity<Page<TicketMinDTO>> findByTitle(Pageable pageable, @Valid @RequestParam String title) {

        return ResponseEntity.ok(service.findByTitle(pageable, title));

    }

    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN')")
    @GetMapping("/searchcategory")
    public ResponseEntity<List<TicketMinDTO>> findByCategory(@Valid @RequestParam String category) {

        return ResponseEntity.ok(service.findByCategory(category));

    }

    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN')")
    @GetMapping("/byoldest")
    public ResponseEntity<Page<TicketMinDTO>> findOldestFirst(Pageable pageable) {

        return ResponseEntity.ok(service.findOldestFirst(pageable));

    }

    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN', 'ROLE_CLIENT')")
    @PostMapping
    public ResponseEntity<TicketMinDTO> insert(@Valid @RequestBody TicketInputDTO dto) {

        TicketMinDTO inserted = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").
                buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(inserted);

    }

    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<TicketMinDTO> update(@PathVariable Long id, @Valid @RequestBody TicketDTO dto) {

        TicketMinDTO updated = service.update(id, dto);

        return ResponseEntity.ok(updated);

    }

    //adicionar patch para o usuario cancelar o chamado se ele quiser

    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN')")
    @PatchMapping(value = "/{id}/status")
    public ResponseEntity<TicketMinDTO> changeStatus(@PathVariable Long id, @Valid @RequestBody TicketPatchDTO dto) {

        TicketMinDTO updated = service.patchStatus(id, dto);

        return ResponseEntity.ok(updated);

    }

    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN')")
    @PatchMapping(value = "/{id}/priority")
    public ResponseEntity<TicketMinDTO> changePriority(@PathVariable Long id, @Valid @RequestBody TicketPatchDTO dto) {

        TicketMinDTO updated = service.patchPriority(id, dto);

        return ResponseEntity.ok(updated);

    }

    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();

    }

}
