package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.services.UserAuthService;
import com.helpdeskspringapi.helpdesk.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private UserAuthService authService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserMinDTO> findById(@PathVariable Long id) {

        UserMinDTO dto = service.findById(id);

        return ResponseEntity.ok(dto);

    }

    @PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_NOC', 'ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> findMe() {

        UserDTO dto = authService.getMe();

        return ResponseEntity.ok(dto);

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Set<UserMinDTO>> findByName(@Valid @RequestParam String name) {

        return ResponseEntity.ok(service.findByName(name));

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserMinDTO>> findAll(Pageable pageable) {


        return ResponseEntity.ok(service.findAll(pageable));

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/searchroles")
    public ResponseEntity<Page<UserMinDTO>> findAllWithRoles(Pageable pageable) {

        Page<UserMinDTO> dto = service.findAllWithRoles(pageable);

        return ResponseEntity.ok(dto);

    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInputDTO dto) {

        UserDTO userDTo = service.insert(dto);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").
                buildAndExpand(userDTo.getId()).toUri()).body(userDTo);

    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserInputDTO dto) {

        return ResponseEntity.ok(service.update(id, dto));

    }

    //adicionar patch pra senha/nome

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();

    }

}
