package com.helpdeskspringapi.helpdesk.dtos.category;

import com.helpdeskspringapi.helpdesk.entities.Category;

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
