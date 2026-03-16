package com.helpdeskspringapi.helpdesk.dtos.user;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleMinDTO;
import com.helpdeskspringapi.helpdesk.entities.User;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public class UserMinDTO {

    @Schema(description = "User id", example = "1")
    private Long id;

    @Schema(description = "Username", example = "marco123")
    private String name;

    private Set<RoleMinDTO> rolesDTO = new HashSet<>();

    public UserMinDTO(Long id, String name, Set<RoleMinDTO> rolesDTO) {
        this.id = id;
        this.name = name;
        this.rolesDTO = rolesDTO;
    }

    public UserMinDTO(User user) {
        id = user.getId();
        name = user.getName();

        for (GrantedAuthority role : user.getRoles()) {
            rolesDTO.add(new RoleMinDTO(role));
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<RoleMinDTO> getRolesDTO() {
        return rolesDTO;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
