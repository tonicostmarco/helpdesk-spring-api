package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserAuthService service;

    public void selfOrAdmin(Long id) {

        User me = service.authenticated();

        if (!me.hasRole("ROLE_ADMIN") && !me.getId().equals(id)) {
            throw new ForbiddenException("Access denied");
        }

    }

}
