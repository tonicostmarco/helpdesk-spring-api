package com.helpdeskspringapi.helpdesk.dtos.category;

import com.helpdeskspringapi.helpdesk.entities.Category;
import io.swagger.v3.oas.annotations.media.Schema;

public class CategoryMinDTO {

    @Schema(description = "Category id", example = "3")
    private Long id;

    @Schema(description = "Category name", example = "Connectivity")
    private String name;

    @Schema(description = "Category description", example = "Issues related to internet, link and signal")
    private String description;

    public CategoryMinDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public CategoryMinDTO(Category category) {
        id = category.getId();
        ;
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
