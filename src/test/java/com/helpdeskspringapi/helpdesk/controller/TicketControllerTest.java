package com.helpdeskspringapi.helpdesk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketPatchDTO;
import com.helpdeskspringapi.helpdesk.exceptions.*;
import com.helpdeskspringapi.helpdesk.factory.TicketFactory;
import com.helpdeskspringapi.helpdesk.services.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;

    private TicketMinDTO minDTO;
    private TicketInputDTO inputDTO;
    private TicketPatchDTO patchDTO;
    private PageImpl<TicketMinDTO> pageMin;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 100L;

        minDTO = TicketFactory.createTicketMinDTO();
        inputDTO = TicketFactory.createTicketInputDTO();
        patchDTO = TicketFactory.createTicketPatchDTO();

        pageMin = new PageImpl<>(List.of(minDTO));

        when(service.findById(existingId)).thenReturn(minDTO);
        when(service.findById(nonExistingId)).thenThrow(new ResourceNotFoundException("Ticket not found"));

        when(service.insert(any(TicketInputDTO.class))).thenReturn(minDTO);
        when(service.update(eq(existingId), any(TicketPatchDTO.class))).thenReturn(minDTO);
        when(service.patchStatus(eq(existingId), any(TicketPatchDTO.class))).thenReturn(minDTO);
        when(service.patchPriority(eq(existingId), any(TicketPatchDTO.class))).thenReturn(minDTO);

        doNothing().when(service).delete(existingId);

        when(service.findAll(any(Pageable.class))).thenReturn(pageMin);
        when(service.findAllWithUsers(any(Pageable.class))).thenReturn(pageMin);
        when(service.findOldestFirst(any(Pageable.class))).thenReturn(pageMin);
    }

    // ---------- GET /tickets ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnPagedTicketsWhenFindAll() throws Exception {
        mockMvc.perform(get("/tickets").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).findAll(any(Pageable.class));
    }

    @Test
    public void shouldReturnUnauthorizedWhenFindAllWithoutAuth() throws Exception {
        mockMvc.perform(get("/tickets").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // ---------- GET /tickets/{id} ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnTicketWhenFindByIdExists() throws Exception {
        mockMvc.perform(get("/tickets/{id}", existingId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        verify(service).findById(existingId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnNotFoundWhenFindByIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/tickets/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).findById(nonExistingId);
    }

    @Test
    public void shouldReturnUnauthorizedWhenFindByIdWithoutAuth() throws Exception {
        mockMvc.perform(get("/tickets/{id}", existingId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // ---------- GET /tickets/me/{id} ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnTicketWhenFindMe() throws Exception {
        when(service.findMe(existingId)).thenReturn(minDTO);

        mockMvc.perform(get("/tickets/me/{id}", existingId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        verify(service).findMe(existingId);
    }

    @Test
    public void shouldReturnUnauthorizedWhenFindMeWithoutAuth() throws Exception {
        mockMvc.perform(get("/tickets/me/{id}", existingId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    // ---------- GET /tickets/searchtitle ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnListWhenFindByTitle() throws Exception {
        String title = "Internet cai do nada";
        when(service.findByTitle(title)).thenReturn(List.of(minDTO));

        mockMvc.perform(get("/tickets/searchtitle").param("title", title).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        verify(service).findByTitle(title);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnNotFoundWhenFindByTitleDoesNotExist() throws Exception {
        String nonExistingTitle = "nonExistingTitle";
        when(service.findByTitle(nonExistingTitle)).thenThrow(new ResourceNotFoundException("not"));

        mockMvc.perform(get("/tickets/searchtitle").param("title", nonExistingTitle).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).findByTitle(nonExistingTitle);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnBadRequestWhenFindByTitleInvalidParam() throws Exception {
        String blank = "  ";
        when(service.findByTitle(blank)).thenThrow(new InvalidParameterException("Title required"));

        mockMvc.perform(get("/tickets/searchtitle").param("title", blank).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service).findByTitle(blank);
    }

    // ---------- GET /tickets/searchcategory ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnListWhenFindByCategory() throws Exception {
        String category = "DNS";
        when(service.findByCategory(category)).thenReturn(List.of(minDTO));

        mockMvc.perform(get("/tickets/searchcategory").param("category", category).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        verify(service).findByCategory(category);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnNotFoundWhenFindByCategoryDoesNotExist() throws Exception {
        String nonExistingCategory = "nonExistingCategory";
        when(service.findByCategory(nonExistingCategory)).thenThrow(new ResourceNotFoundException("cat not"));

        mockMvc.perform(get("/tickets/searchcategory").param("category", nonExistingCategory).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).findByCategory(nonExistingCategory);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnBadRequestWhenFindByCategoryInvalidParam() throws Exception {
        String blank = "  ";
        when(service.findByCategory(blank)).thenThrow(new InvalidParameterException("Category required"));

        mockMvc.perform(get("/tickets/searchcategory").param("category", blank).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service).findByCategory(blank);
    }

    // ---------- GET /tickets/searchusers and /byoldest ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnPagedTicketsWithUsers() throws Exception {
        mockMvc.perform(get("/tickets/searchusers").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).findAllWithUsers(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnOldestFirstPagedTickets() throws Exception {
        mockMvc.perform(get("/tickets/byoldest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).findOldestFirst(any(Pageable.class));
    }

    // ---------- POST /tickets ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldInsertWhenValid() throws Exception {
        String json = objectMapper.writeValueAsString(inputDTO);

        mockMvc.perform(post("/tickets").with(csrf())
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        verify(service).insert(any(TicketInputDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnBadRequestWhenInsertWithInvalidInput() throws Exception {
        TicketInputDTO invalid = TicketFactory.createTicketInputDTO();
        invalid.setTitle("  ");
        invalid.setCategories(Set.of());

        String jsonBody = objectMapper.writeValueAsString(invalid);

        mockMvc.perform(post("/tickets").with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).insert(any(TicketInputDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnNotFoundWhenInsertWithInvalidCategories() throws Exception {
        when(service.insert(any(TicketInputDTO.class))).thenThrow(ResourceNotFoundException.class);

        String jsonBody = objectMapper.writeValueAsString(inputDTO);

        mockMvc.perform(post("/tickets").with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).insert(any(TicketInputDTO.class));
    }

    // ---------- PUT /tickets/{id} ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateWhenIdExistsAndDataValid() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(put("/tickets/{id}", existingId).with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        verify(service).update(eq(existingId), any(TicketPatchDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnNotFoundWhenUpdateIdDoesNotExist() throws Exception {
        when(service.update(eq(nonExistingId), any(TicketPatchDTO.class))).thenThrow(ResourceNotFoundException.class);

        String jsonBody = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(put("/tickets/{id}", nonExistingId).with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).update(eq(nonExistingId), any(TicketPatchDTO.class));
    }

    // ---------- PATCH /{id}/status ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateStatusWhenIdExistsAndDataValid() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/tickets/{id}/status", existingId).with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        verify(service).patchStatus(eq(existingId), any(TicketPatchDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnBadRequestWhenPatchStatusBusinessException() throws Exception {
        when(service.patchStatus(eq(existingId), any(TicketPatchDTO.class))).thenThrow(new BusinessException("Wasn't able to change"));

        String jsonBody = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/tickets/{id}/status", existingId).with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service).patchStatus(eq(existingId), any(TicketPatchDTO.class));
    }

    // ---------- PATCH /{id}/priority ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdatePriorityWhenIdExistsAndDataValid() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(patchDTO);

        mockMvc.perform(patch("/tickets/{id}/priority", existingId).with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        verify(service).patchPriority(eq(existingId), any(TicketPatchDTO.class));
    }

    // ---------- DELETE /tickets/{id} ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteWhenIdExists() throws Exception {
        mockMvc.perform(delete("/tickets/{id}", existingId).with(csrf()))
                .andExpect(status().isNoContent());

        verify(service).delete(existingId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnBadRequestWhenDeleteDatabaseException() throws Exception {
        doNothing().when(service).delete(existingId);
        doThrow(new DatabaseException("Referential integrity failure")).when(service).delete(existingId);

        mockMvc.perform(delete("/tickets/{id}", existingId).with(csrf()))
                .andExpect(status().isBadRequest());

        verify(service).delete(existingId);
    }
}
