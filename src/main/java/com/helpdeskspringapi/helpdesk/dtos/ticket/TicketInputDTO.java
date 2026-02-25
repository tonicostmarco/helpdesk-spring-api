package com.helpdeskspringapi.helpdesk.dtos.ticket;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class TicketInputDTO {

    private Long id;

    @Size(min = 3, max = 30, message = "Titl must have between 4 and 30 characters")
    @NotBlank(message = "Title must be filled")
    private String title;

    @Size(min = 5, max = 150, message = "Description must have between 5 and 15 characters")
    @NotBlank(message = "Title must be filled")
    private String description;

    @NotEmpty(message = "Category required")
    private Set<CategoryDTO> categories = new HashSet<>();

    public TicketInputDTO() {

    }

    public String getTitle() {
        return title;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategories(Set<CategoryDTO> categories) {
        this.categories = categories;
    }
}

