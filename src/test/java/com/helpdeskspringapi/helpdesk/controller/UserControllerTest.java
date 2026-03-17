package com.helpdeskspringapi.helpdesk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpdeskspringapi.helpdesk.dtos.user.UserDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.user.UserMinDTO;
import com.helpdeskspringapi.helpdesk.exceptions.BusinessException;
import com.helpdeskspringapi.helpdesk.exceptions.DatabaseException;
import com.helpdeskspringapi.helpdesk.exceptions.InvalidParameterException;
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.factory.UserFactory;
import com.helpdeskspringapi.helpdesk.services.UserAuthService;
import com.helpdeskspringapi.helpdesk.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserAuthService authService;
    // ---------- GET /users/{id} ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUserWhenFindById() throws Exception {
        UserMinDTO dto = UserFactory.createUserMinDTO();
        when(userService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.name").value(dto.getName()));

        verify(userService).findById(1L);
    }

    @Test
    void shouldReturnUnauthorizedWhenFindByIdWithoutAuth() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).findById(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenFindByIdThrowsResourceNotFoundException() throws Exception {
        when(userService.findById(1L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());

        verify(userService).findById(1L);
    }

    // ---------- GET /users/me ----------

    @Test
    @WithMockUser(roles = "SUPPORT")
    void shouldReturnUserWhenFindMe() throws Exception {
        UserDTO dto = UserFactory.createUserDTO();
        when(authService.getMe()).thenReturn(dto);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.name").value(dto.getName()));

        verify(authService).getMe();
    }

    @Test
    void shouldReturnUnauthorizedWhenFindMeWithoutAuth() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());

        verify(authService, never()).getMe();
    }

    @Test
    @WithMockUser(roles = "SUPPORT")
    void shouldReturnNotFoundWhenFindMeThrowsResourceNotFoundException() throws Exception {
        when(authService.getMe()).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isNotFound());

        verify(authService).getMe();
    }

    // ---------- GET /users/search ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUsersWhenFindByName() throws Exception {
        UserMinDTO dto = UserFactory.createUserMinDTO();
        when(userService.findByName("marco")).thenReturn(java.util.Set.of(dto));

        mockMvc.perform(get("/users/search").param("name", "marco"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto.getId()))
                .andExpect(jsonPath("$[0].name").value(dto.getName()));

        verify(userService).findByName("marco");
    }

    @Test
    void shouldReturnUnauthorizedWhenFindByNameWithoutAuth() throws Exception {
        mockMvc.perform(get("/users/search").param("name", "marco"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).findByName(anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenFindByNameWithBlankName() throws Exception {
        when(userService.findByName("")).thenThrow(new InvalidParameterException("Name required"));

        mockMvc.perform(get("/users/search").param("name", ""))
                .andExpect(status().isBadRequest());

        verify(userService).findByName("");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenFindByNameThrowsResourceNotFoundException() throws Exception {
        when(userService.findByName("noone")).thenThrow(new ResourceNotFoundException("Name not found"));

        mockMvc.perform(get("/users/search").param("name", "noone"))
                .andExpect(status().isNotFound());

        verify(userService).findByName("noone");
    }

    // ---------- GET /users ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnPageWhenFindAll() throws Exception {
        UserMinDTO dto = UserFactory.createUserMinDTO();
        when(userService.findAll(any())).thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(dto.getId()));

        verify(userService).findAll(any());
    }

    @Test
    void shouldReturnUnauthorizedWhenFindAllWithoutAuth() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).findAll(any());
    }

    // ---------- GET /users/searchroles ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnListWhenFindAllWithRoles() throws Exception {
        UserMinDTO dto = UserFactory.createUserMinDTO();
        when(userService.findAllWithRoles()).thenReturn(List.of(dto));

        mockMvc.perform(get("/users/searchroles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dto.getId()));

        verify(userService).findAllWithRoles();
    }

    @Test
    void shouldReturnUnauthorizedWhenFindAllWithRolesWithoutAuth() throws Exception {
        mockMvc.perform(get("/users/searchroles"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).findAllWithRoles();
    }

    // ---------- POST /users ----------

    @Test
    @WithMockUser
    void shouldReturnCreatedWhenInsertUser() throws Exception {
        UserInputDTO input = UserFactory.createUserInputDTO();
        UserDTO dto = UserFactory.createUserDTO();
        when(userService.insert(any(UserInputDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.name").value(dto.getName()));

        verify(userService).insert(any(UserInputDTO.class));
    }

    @Test
    void shouldReturnUnauthorizedWhenInsertWithoutAuth() throws Exception {
        UserInputDTO input = UserFactory.createUserInputDTO();
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).insert(any());
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenInsertUserWithInvalidInput() throws Exception {
        UserInputDTO input = new UserInputDTO();
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).insert(any());
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenInsertUserThrowsBusinessException() throws Exception {
        UserInputDTO input = UserFactory.createUserInputDTO();
        when(userService.insert(any(UserInputDTO.class))).thenThrow(new BusinessException("Email already registered"));
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());

        verify(userService).insert(any(UserInputDTO.class));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenInsertUserThrowsResourceNotFoundException() throws Exception {
        UserInputDTO input = UserFactory.createUserInputDTO();
        when(userService.insert(any(UserInputDTO.class))).thenThrow(new ResourceNotFoundException("Role id invalid"));
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());

        verify(userService).insert(any(UserInputDTO.class));
    }

    // ---------- PUT /users/{id} ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnOkWhenUpdateUser() throws Exception {
        UserInputDTO input = UserFactory.createUserInputDTO();
        UserDTO dto = UserFactory.createUserDTO();
        when(userService.update(eq(1L), any(UserInputDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()));

        verify(userService).update(eq(1L), any(UserInputDTO.class));
    }

    @Test
    void shouldReturnUnauthorizedWhenUpdateWithoutAuth() throws Exception {
        UserInputDTO input = UserFactory.createUserInputDTO();
        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).update(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUpdateWithInvalidInput() throws Exception {
        UserInputDTO input = new UserInputDTO();
        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenUpdateThrowsResourceNotFoundException() throws Exception {
        UserInputDTO input = UserFactory.createUserInputDTO();
        when(userService.update(eq(1L), any())).thenThrow(new ResourceNotFoundException("User not found"));
        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());

        verify(userService).update(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUpdateThrowsBusinessException() throws Exception {
        UserInputDTO input = UserFactory.createUserInputDTO();
        when(userService.update(eq(1L), any())).thenThrow(new BusinessException("Email already registered"));
        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());

        verify(userService).update(eq(1L), any());
    }

    // ---------- DELETE /users/{id} ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNoContentWhenDelete() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    void shouldReturnUnauthorizedWhenDeleteWithoutAuth() throws Exception {
        mockMvc.perform(delete("/users/1").with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenDeleteThrowsResourceNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("Id not found")).when(userService).delete(1L);
        mockMvc.perform(delete("/users/1").with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).delete(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenDeleteThrowsDatabaseException() throws Exception {
        doThrow(new DatabaseException("Referential integrity failure")).when(userService).delete(1L);

        mockMvc.perform(delete("/users/1").with(csrf()))
                .andExpect(status().isBadRequest());

        verify(userService).delete(1L);
    }

}