package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Set;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserMinDTO> findById(@PathVariable Long id){

        UserMinDTO dto = service.findById(id);

        return ResponseEntity.ok(dto);

    }

    @GetMapping("/search")
    public ResponseEntity<Set<UserMinDTO>> findByName(@Valid @RequestParam String name){

        return ResponseEntity.ok(service.findByName(name));

    }

    @GetMapping
    public ResponseEntity<Page<UserMinDTO>> findAll(Pageable pageable){


        return ResponseEntity.ok(service.findAll(pageable));

    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@RequestBody UserInputDTO dto) {

        UserDTO userDTo = service.insert(dto);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").
                buildAndExpand(userDTo.getId()).toUri()).body(userDTo);

    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserInputDTO dto){

        return ResponseEntity.ok(service.update(id, dto));

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        service.delete(id);

        return ResponseEntity.noContent().build();

    }

}
