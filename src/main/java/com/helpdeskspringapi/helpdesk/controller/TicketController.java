package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/tickets")
public class TicketController {

    @Autowired
    private TicketService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<TicketMinDTO> findById(@PathVariable @Valid Long id){

        TicketMinDTO dto = service.findById(id);

        return ResponseEntity.ok(dto);

    }

    @GetMapping
    public ResponseEntity<Page<TicketMinDTO>> findAll(Pageable pageable){

        Page<TicketMinDTO> dto = service.findAll(pageable);

        return ResponseEntity.ok(dto);

    }

    @GetMapping("/searchtitle")
    public ResponseEntity<Page<TicketMinDTO>> findByTitle(Pageable pageable, @Valid @RequestParam String title){

        return ResponseEntity.ok(service.findByTitle(pageable, title));

    }

    @GetMapping("/searchcategory")
    public ResponseEntity<Page<TicketMinDTO>> findByCategory(Pageable pageable, @Valid @RequestParam String category){

        return ResponseEntity.ok(service.findByCategory(pageable, category));

    }

    @GetMapping("/byoldest")
    public ResponseEntity<Page<TicketMinDTO>> findOldestFirst(Pageable pageable){

        return ResponseEntity.ok(service.findOldestFirst(pageable));

    }

    @PostMapping
    public ResponseEntity<TicketDTO> insert(@Valid @RequestBody TicketDTO dto) {

        dto = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").
                buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(dto);

    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<TicketDTO> update(@PathVariable Long id, @Valid @RequestBody TicketDTO dto){

        dto = service.update(id, dto);

        return ResponseEntity.ok(dto);

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable Long id){

        service.delete(id);

        return ResponseEntity.noContent().build();

    }

}
