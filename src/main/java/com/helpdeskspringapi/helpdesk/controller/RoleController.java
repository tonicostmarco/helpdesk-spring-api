package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleDTO;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleMinDTO;
import com.helpdeskspringapi.helpdesk.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

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
    public ResponseEntity<List<RoleMinDTO>> findAll(){

        List<RoleMinDTO> dto = service.findAll();

        return ResponseEntity.ok(dto);

    }

    @PostMapping
    public ResponseEntity<RoleMinDTO> insert(@RequestBody RoleDTO dto) {

        RoleMinDTO roleDTO = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").
                buildAndExpand(roleDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(roleDTO);

    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<RoleMinDTO> update(@PathVariable Long id, @RequestBody RoleDTO dto){

        RoleMinDTO roleDTO = service.update(id, dto);

        return ResponseEntity.ok(roleDTO);

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        service.delete(id);

        return ResponseEntity.noContent().build();

    }
}
