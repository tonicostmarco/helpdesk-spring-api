package com.helpdeskspringapi.helpdesk.controller;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryMinDTO> findById(@PathVariable Long id){

        CategoryMinDTO dto = service.findById(id);

        return ResponseEntity.ok(dto);

    }

    @GetMapping
    public ResponseEntity<List<CategoryMinDTO>> findAll(){

        List<CategoryMinDTO> dto = service.findAll();

        return ResponseEntity.ok(dto);

    }

    @GetMapping("/searchtickets")
    public ResponseEntity<List<CategoryMinDTO>> findAllWithTickets(Pageable pageable){

        List<CategoryMinDTO> dto = service.findAllWithTickets();

        return ResponseEntity.ok(dto);

    }

    @PostMapping
    public ResponseEntity<CategoryMinDTO> insert(@RequestBody CategoryMinDTO dto) {

        dto = service.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").
                buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(dto);

    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoryMinDTO> update(@PathVariable Long id, @RequestBody CategoryMinDTO dto){

        dto = service.update(id, dto);

        return ResponseEntity.ok(dto);

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        service.delete(id);

        return ResponseEntity.noContent().build();

    }

}
