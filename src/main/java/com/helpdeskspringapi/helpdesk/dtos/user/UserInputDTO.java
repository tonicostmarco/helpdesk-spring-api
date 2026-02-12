package com.helpdeskspringapi.helpdesk.dtos.user;

import com.helpdeskspringapi.helpdesk.dtos.role.RoleDTO;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

public class UserInputDTO {


    private Long id;

    @Size(min = 3, max = 15, message = "Username must have between 5 and 15 characters")
    @NotBlank(message = "Required field")
    private String name;

    @Size(min = 3, max = 80, message = "Name must have between 3 and 80 characters")
    @Email(message = "Insert a valid e-mail")
    @NotBlank(message = "Required field")
    private String email;

    @NotBlank(message = "User must have a phone")
    private String phone;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).+$",
            message = "Password must have upper, lower, number and symbol"
    )
    @NotBlank(message = "Required field")
    private String password;

    @NotEmpty(message = "User must have a role")
    private Set<RoleDTO> roles = new HashSet<>();

    public UserInputDTO() {
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
