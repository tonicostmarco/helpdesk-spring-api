package com.helpdeskspringapi.helpdesk.repositories;

import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.entities.User;
import com.helpdeskspringapi.helpdesk.factory.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.Set;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private String existingEmail;
    private String nonExistingEmail;

    private String existingName;
    private String nonExistingName;

    private User user;
    private UserMinDTO userMin;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 100L;

        existingEmail = "admin@helpdesk.com";
        nonExistingEmail = "nonExistingEmail";

        existingName = "Marco Admin";
        nonExistingName = "nonExistingName";

        user = UserFactory.createUser();
        userMin = UserFactory.createUserMinDTO();

    }

    @Test
    public void shouldReturnUserWhenIdExists() {

        Optional<User> u = repository.findById(existingId);

        Assertions.assertNotNull(u);
        Assertions.assertEquals(existingId, u.get().getId());

    }

    @Test
    public void shouldReturnEmptyWhenIdDoesNotExist() {

        Optional<User> u = repository.findById(nonExistingId);

        Assertions.assertTrue(u.isEmpty());

    }

    @Test
    public void shouldReturnUserWhenEmailExists() {

        Optional<User> u = repository.findByEmail(existingEmail);

        Assertions.assertNotNull(u);
        Assertions.assertEquals(existingEmail, u.get().getEmail());

    }

    @Test
    public void shouldReturnEmptyWhenEmailDoesNotExist() {

        Optional<User> u = repository.findByEmail(nonExistingEmail);

        Assertions.assertTrue(u.isEmpty());

    }

    @Test
    public void shouldReturnUserWhenNameExists() {

        Set<UserMinDTO> u = repository.findByName(existingName);

        Assertions.assertNotNull(u);
        Assertions.assertEquals(existingName, u.stream().findFirst().get().getName());

    }

    @Test
    public void shouldReturnEmptyWhenNameDoesNotExist() {

        Set<UserMinDTO> u = repository.findByName(nonExistingName);

        Assertions.assertTrue(u.isEmpty());

    }

    @Test
    public void shouldReturnUserWithRoles() {

        //Set<UserMinDTO> u = repository.findUserWithRoles();

        //Assertions.assertNotNull(u);
        //Assertions.assertEquals(existingName, u.stream().findFirst().get().getName());

    }

}
