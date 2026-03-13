package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketPatchDTO;
import com.helpdeskspringapi.helpdesk.services.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
            description = "Find a Ticket by id",
            summary = "Get a ticket by id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            }
    )
    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN', 'ROLE_SUPPORT', 'ROLE_CLIENT')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<TicketMinDTO> findById(@PathVariable Long id) {

        TicketMinDTO dto = service.findById(id);

        return ResponseEntity.ok(dto);

    }

    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_CLIENT')")
    @GetMapping(value = "/me/{id}")
    public ResponseEntity<TicketMinDTO> findMe(@PathVariable Long id) {

        TicketMinDTO dto = service.findMe(id);

        return ResponseEntity.ok(dto);

    }

    @Operation(
            description = "Find all Tickets with pagination",
            summary = "Get all tickets with pagination",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<TicketMinDTO>> findAll(@Valid Pageable pageable) {

        Page<TicketMinDTO> dto = service.findAll(pageable);

        return ResponseEntity.ok(dto);

    }

    @Operation(
            description = "Find all Tickets with pagination including users",
            summary = "Get all tickets with pagination and user data",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN')")
    @GetMapping("/searchusers")
    public ResponseEntity<Page<TicketMinDTO>> findAllWithUsers(Pageable pageable) {

        Page<TicketMinDTO> dto = service.findAllWithUsers(pageable);

        return ResponseEntity.ok(dto);

    }

    @Operation(
            description = "Search Tickets by title with pagination",
            summary = "Search tickets by title",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN', 'ROLE_SUPPORT')")
    @GetMapping("/searchtitle")
    public ResponseEntity<List<TicketMinDTO>> findByTitle(@Valid @RequestParam String title) {

        return ResponseEntity.ok(service.findByTitle(title));

    }

    @Operation(
            description = "Search Tickets by category",
            summary = "Search tickets by category",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN', 'ROLE_SUPPORT')")
    @GetMapping("/searchcategory")
    public ResponseEntity<List<TicketMinDTO>> findByCategory(@Valid @RequestParam String category) {

        return ResponseEntity.ok(service.findByCategory(category));

    }

    @Operation(
            description = "Find Tickets ordered by oldest first with pagination",
            summary = "Get tickets ordered by oldest first",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_NOC', 'ROLE_ADMIN', 'ROLE_SUPPORT')")
    @GetMapping("/byoldest")
    public ResponseEntity<Page<TicketMinDTO>> findOldestFirst(Pageable pageable) {

        return ResponseEntity.ok(service.findOldestFirst(pageable));

    }

    @Operation(
            description = "Create a new Ticket",
            summary = "Create a new ticket",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN', 'ROLE_CLIENT')")
    @PostMapping(produces = "application/json")
    public ResponseEntity<TicketMinDTO> insert(@Valid @RequestBody TicketInputDTO dto) {

        TicketMinDTO inserted = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").
                buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(inserted);

    }

    @Operation(
            description = "Update a Ticket by id",
            summary = "Update a ticket",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN')")
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<TicketMinDTO> update(@PathVariable Long id, @Valid @RequestBody TicketPatchDTO dto) {

        TicketMinDTO updated = service.update(id, dto);

        return ResponseEntity.ok(updated);

    }

    @Operation(
            description = "Change Ticket status by id",
            summary = "Patch ticket status",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN')")
    @PatchMapping(value = "/{id}/status", produces = "application/json")
    public ResponseEntity<TicketMinDTO> changeStatus(@PathVariable Long id, @Valid @RequestBody TicketPatchDTO dto) {

        TicketMinDTO updated = service.patchStatus(id, dto);

        return ResponseEntity.ok(updated);

    }

    @Operation(
            description = "Change Ticket priority by id",
            summary = "Patch ticket priority",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN')")
    @PatchMapping(value = "/{id}/priority", produces = "application/json")
    public ResponseEntity<TicketMinDTO> changePriority(@PathVariable Long id, @Valid @RequestBody TicketPatchDTO dto) {

        TicketMinDTO updated = service.patchPriority(id, dto);

        return ResponseEntity.ok(updated);

    }

    @Operation(
            description = "Delete a Ticket by id",
            summary = "Delete a ticket",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not Found", responseCode = "404")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Void> delete(@Valid @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();

    }

}
