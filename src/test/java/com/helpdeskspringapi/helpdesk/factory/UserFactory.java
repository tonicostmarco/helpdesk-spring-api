package com.helpdeskspringapi.helpdesk.factory;

import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.dtos.role.RoleDTO;
import com.helpdeskspringapi.helpdesk.entities.User;

import java.util.Set;

public class UserFactory {

    public static User createUser() {
        return new User(
                "Marco Admin",
                19,
                "991731543",
                "admin@helpdesk.com",
                "$2b$10$8orZfrgp/uRwNstcqzYmI.jtGSlcpLEugS0xk1wefRW2KUOkEuuf2",
                Set.of(new Role(1L, "ROLE_ADMIN"))
        );
    }

    public static UserDTO createUserDTO() {
        return new UserDTO(createUser());
    }
    public static UserMinDTO createUserMinDTO() {
        return new UserMinDTO(createUser());
    }

    public static UserInputDTO createUserInputDTO() {

        UserInputDTO dto = new UserInputDTO();

        dto.setName("Bruno NOC");
        dto.setEmail("noc@helpdesk.com");
        dto.setDdd(11);
        dto.setPhone("900000002");
        dto.setPassword("$2b$10$Mpzz35YK9HNl9jYnCzUfGOTcCO6m9VCZN5LLf0A2h95hHgqmIbVmS");
        dto.setRoles(Set.of(new RoleDTO(1L, "ROLE_ADMIN", Set.of())));

        return dto;
    }

}
