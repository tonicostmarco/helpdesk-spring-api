package com.helpdeskspringapi.helpdesk.dtos;

import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;

import java.util.HashSet;
import java.util.Set;

public class CategoryMinDTO {

    private Long id;
    private String name;

    public CategoryMinDTO(Long id, String name) {
        this.name = name;
    }

    public CategoryMinDTO(Category category) {
        name = category.getName();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
