package com.helpdeskspringapi.helpdesk.dtos;

import com.helpdeskspringapi.helpdesk.entities.User;

public class UserMinDTO {

    private Long id;
    private String name;

    public UserMinDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserMinDTO(User user) {
        id = user.getId();
        name = user.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
