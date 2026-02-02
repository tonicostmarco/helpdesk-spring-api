package com.helpdeskspringapi.helpdesk.dtos;

import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {

    private Long id;
    private String name;

    private String email;
    private String phone;

    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(Long id, String name, String email, String phone, String password, Set<RoleDTO> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.roles = roles;
    }

    public UserDTO(User user) {

        id = user.getId();
        name = user.getName();
        email = user.getEmail();
        phone = user.getPhone();

        for (Role role : user.getRoles()) {
            roles.add(new RoleDTO(role));
        }
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }


    public Set<RoleDTO> getRoles() {
        return roles;
    }

    }
