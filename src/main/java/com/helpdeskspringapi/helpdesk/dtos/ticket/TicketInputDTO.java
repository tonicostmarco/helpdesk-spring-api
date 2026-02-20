package com.helpdeskspringapi.helpdesk.dtos.ticket;

import com.helpdeskspringapi.helpdesk.dtos.category.CategoryDTO;

import java.util.HashSet;
import java.util.Set;

public class TicketInputDTO {

    private Long id;
    private String title;
    private String description;
    private Set<CategoryDTO> categories = new HashSet<>();

    public TicketInputDTO() {

    }


        public String getTitle () {
            return title;
        }

        public void setTitle (String title){
            this.title = title;
        }

        public Long getId () {
            return id;
        }

        public void setId (Long id){
            this.id = id;
        }

        public String getDescription () {
            return description;
        }

        public void setDescription (String description){
            this.description = description;
        }

        public Set<CategoryDTO> getCategories () {
            return categories;
        }

        public void setCategories (Set < CategoryDTO > categories) {
            this.categories = categories;
        }
    }

