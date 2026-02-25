package com.helpdeskspringapi.helpdesk.dtos.role;

import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;

import java.util.HashSet;
import java.util.Set;

public class RoleDTO {

    private Long id;
    private String authority;

    private Set<UserMinDTO> users = new HashSet<>();

    public RoleDTO() {
    }

    public RoleDTO(Long id, String authority, Set<UserMinDTO> users) {
        this.id = id;
        this.authority = authority;
        this.users = users;
    }

    public RoleDTO(Role role) {
        id = role.getId();
        authority = role.getAuthority();

        for (User user : role.getUsers()) {
            users.add(new UserMinDTO(user));
        }

    }

    public Long getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }

    public Set<UserMinDTO> getUsers() {
        return users;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void setUsers(Set<UserMinDTO> users) {
        this.users = users;
    }
}
