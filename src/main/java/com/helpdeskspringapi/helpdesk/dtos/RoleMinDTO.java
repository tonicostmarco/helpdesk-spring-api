package com.helpdeskspringapi.helpdesk.dtos;

import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;

import java.util.HashSet;
import java.util.Set;

public class RoleMinDTO {

    private Long id;
    private String authority;

    public RoleMinDTO(Long id, String authority) {
        this.authority = authority;
    }

    public RoleMinDTO(Role role) {
        id = role.getId();
        authority = role.getAuthority();

      }

    public Long getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

}
