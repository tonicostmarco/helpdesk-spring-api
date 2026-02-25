package com.helpdeskspringapi.helpdesk.dtos.role;

import org.springframework.security.core.GrantedAuthority;

public class RoleMinDTO {

    private String authority;

    public RoleMinDTO(String authority) {
        this.authority = authority;
    }

    public RoleMinDTO(GrantedAuthority role) {
        authority = role.getAuthority();

    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

}
