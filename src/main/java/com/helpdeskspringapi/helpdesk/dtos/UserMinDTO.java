package com.helpdeskspringapi.helpdesk.dtos;

import com.helpdeskspringapi.helpdesk.entities.User;

public class UserMinDTO {

    private String name;

    public UserMinDTO(String name) {
        this.name = name;
    }

    public UserMinDTO(User user) {

        name = user.getName();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
