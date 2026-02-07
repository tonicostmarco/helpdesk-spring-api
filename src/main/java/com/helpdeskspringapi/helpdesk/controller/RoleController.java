package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleDTO;

import com.helpdeskspringapi.helpdesk.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/roles")
public class RoleController {


    @Autowired
    private RoleService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> findById(@PathVariable Long id){

        RoleDTO dto = service.findById(id);

        return ResponseEntity.ok(dto);

    }

    @GetMapping
    public ResponseEntity<Page<RoleDTO>> findAll(Pageable pageable){

        Page<RoleDTO> dto = service.findAll(pageable);

        return ResponseEntity.ok(dto);

    }

    @PostMapping
    public ResponseEntity<RoleDTO> insert(@RequestBody RoleDTO dto) {

        RoleDTO roleDTO = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").
                buildAndExpand(roleDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(dto);

    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable Long id, @RequestBody RoleDTO dto){

        RoleDTO roleDTO = service.update(id, dto);

        return ResponseEntity.ok(roleDTO);

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        service.delete(id);

        return ResponseEntity.noContent().build();

    }
}
