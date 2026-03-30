package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.services.UserAuthService;
import com.helpdeskspringapi.helpdesk.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService service;
    private final UserAuthService authService;

    public UserController(UserService service, UserAuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserMinDTO> findById(@PathVariable Long id) {

        UserMinDTO dto = service.findById(id);

        return ResponseEntity.ok(dto);

    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPPORT', 'NOC', 'ADMIN', 'CLIENT')")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> findMe() {

        UserDTO dto = authService.getMe();

        return ResponseEntity.ok(dto);

    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Set<UserMinDTO>> findByName(@Valid @RequestParam String name) {

        return ResponseEntity.ok(service.findByName(name));

    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserMinDTO>> findAll(Pageable pageable) {


        return ResponseEntity.ok(service.findAll(pageable));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/searchroles")
    public ResponseEntity<List<UserMinDTO>> findAllWithRoles() {

        List<UserMinDTO> dto = service.findAllWithRoles();

        return ResponseEntity.ok(dto);

    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'NOC', 'SUPPORT')")
    @PostMapping(produces = "application/json")
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInputDTO dto) {

        UserDTO userDTo = service.insert(dto);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").
                buildAndExpand(userDTo.getId()).toUri()).body(userDTo);

    }

    @Operation(
            description = "Update an user",
            summary = "Update completely an user",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserInputDTO dto) {

        return ResponseEntity.ok(service.update(id, dto));

    }

    @Operation(
            description = "Delete an user by id",
            summary = "Delete user",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "204"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();

    }

}
