package com.helpdeskspringapi.helpdesk.dtos;

import java.util.HashSet;
import java.util.Set;

public class UserCreateDTO {
    private Long id;
    private String name;

    private String email;
    private String phone;
    private String password;

    private Set<RoleDTO> roles = new HashSet<>();

    public UserCreateDTO() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }

}
