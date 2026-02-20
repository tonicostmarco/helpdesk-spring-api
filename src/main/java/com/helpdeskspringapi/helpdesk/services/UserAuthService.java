package com.helpdeskspringapi.helpdesk.services;

import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.entities.Role;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.projections.UserDetailsProjection;
import com.helpdeskspringapi.helpdesk.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAuthService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> users = repository.searchUserAndRolesByEmail(username);

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("Email not found");
        }

        User user = new User();

        user.setEmail(username);
        user.setPassword(users.getFirst().getPassword());

        for (UserDetailsProjection u : users) {
            user.addRole(new Role(u.getRoleId(), u.getAuthority()));
        }

        return user;
    }

    protected User authenticated() {

        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");

           return repository.findByEmail(username).get();

        }
        catch (Exception e) {
            throw new UsernameNotFoundException("User must be logged");
        }

    }

    @Transactional(readOnly = true)
    public UserDTO getMe() {

        User user = authenticated();
        return new UserDTO(user);


    }

    @Transactional(readOnly = true)
    protected User getMeU() {

        return authenticated();


    }


}
