package com.helpdeskspringapi.helpdesk.dtos.category;

import com.helpdeskspringapi.helpdesk.entities.Category;

public class CategoryMinDTO {

    private Long id;
    private String name;
    private String description;

    public CategoryMinDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public CategoryMinDTO(Category category) {
        id = category.getId();;
        name = category.getName();
        description = category.getDescription();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
