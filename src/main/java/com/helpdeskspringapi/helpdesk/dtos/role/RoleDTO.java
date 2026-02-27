package com.helpdeskspringapi.helpdesk.dtos.role;

import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;

public class RoleDTO {

    @Schema(description = "Role id", example = "1")
    private Long id;

    @Schema(description = "Role authority", example = "ROLE_ADMIN")
    private String authority;

    @Schema(description = "Users that have this role (minimal)")
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
