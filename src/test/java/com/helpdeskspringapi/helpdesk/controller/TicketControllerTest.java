package com.helpdeskspringapi.helpdesk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketInputDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketMinDTO;
import com.helpdeskspringapi.helpdesk.dtos.ticket.TicketPatchDTO;
import com.helpdeskspringapi.helpdesk.entities.Category;
import com.helpdeskspringapi.helpdesk.entities.Ticket;
import com.helpdeskspringapi.helpdesk.exceptions.ResourceNotFoundException;
import com.helpdeskspringapi.helpdesk.factory.TicketFactory;
import com.helpdeskspringapi.helpdesk.services.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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
    private Long dependentId;
    private Set<Long> ids;
    private Set<Long> wrongIds;
    private String expectedTitle;
    private String nonExistingTitle;
    private String expectedCategory;
    private String nonExistingCategory;

    private Ticket ticket;
    private TicketDTO ticketDTO;
    private TicketMinDTO minDTO;
    private TicketInputDTO inputDTO;
    private TicketPatchDTO patchDTO;
    private List<TicketMinDTO> listMinDTO;
    private List<Category> categories;

    private PageImpl<Ticket> page;
    private PageImpl<TicketMinDTO> pageMin;
    private Pageable pageable;

    @BeforeEach
    void setUp() {

        existingId = 1L;
        nonExistingId = 100L;
        expectedTitle = "Internet cai do nada";
        nonExistingTitle = "nonExistingTitle";
        expectedCategory = "DNS";
        nonExistingCategory = "nonExistingCategory";

        ticket = TicketFactory.createTicket();
        ticketDTO = TicketFactory.createTicketDTO();
        minDTO = TicketFactory.createTicketMinDTO();
        inputDTO = TicketFactory.createTicketInputDTO();
        patchDTO = TicketFactory.createTicketPatchDTO();

        listMinDTO = List.of(minDTO);

        page = new PageImpl<>(List.of(ticket));
        pageMin = new PageImpl<>(List.of(minDTO));
        pageable = PageRequest.of(0, 10);

        when(service.findById(existingId)).thenReturn(minDTO);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(service.insert(any(TicketInputDTO.class))).thenReturn(minDTO);
        when(service.update(eq(existingId), any(TicketPatchDTO.class))).thenReturn(minDTO);
        when(service.patchStatus(eq(existingId), any(TicketPatchDTO.class))).thenReturn(minDTO);
        when(service.patchPriority(eq(existingId), any(TicketPatchDTO.class))).thenReturn(minDTO);

        doNothing().when(service).delete(existingId);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findAllShouldReturnPagedTicket() throws Exception {

        ResultActions result = mockMvc.perform(get("/tickets", pageable)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findByIdShouldReturnTicketWhenIdExists() throws Exception {

        ResultActions result = mockMvc.perform(get("/tickets/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.title").exists());
        result.andExpect(jsonPath("$.client").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.priority").exists());
        result.andExpect(jsonPath("$.createdAt").exists());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findByIdShouldReturnNothingWhenIdDoesNotExists() throws Exception {

        ResultActions result = mockMvc.perform(get("/tickets/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldInsertTicketWhenCorrectData() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(inputDTO);

        ResultActions result = mockMvc.perform(post("/tickets").with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.title").exists());
        result.andExpect(jsonPath("$.client").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.priority").exists());
        result.andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateTicketWhenCorrectIdAndData() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(patchDTO);

        ResultActions result = mockMvc.perform(put("/tickets/{id}", existingId).with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.title").exists());
        result.andExpect(jsonPath("$.client").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.priority").exists());
        result.andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateStatusWhenCorrectIdAndData() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(patchDTO);

        ResultActions result = mockMvc.perform(patch("/tickets/{id}/status", existingId).with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.title").exists());
        result.andExpect(jsonPath("$.client").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.priority").exists());
        result.andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdatePriorityWhenCorrectIdAndData() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(patchDTO);

        ResultActions result = mockMvc.perform(patch("/tickets/{id}/priority", existingId).with(csrf())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.title").exists());
        result.andExpect(jsonPath("$.client").exists());
        result.andExpect(jsonPath("$.status").exists());
        result.andExpect(jsonPath("$.priority").exists());
        result.andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteTicketWhenIdExists() throws Exception {

        ResultActions result = mockMvc.perform(delete("/tickets/{id}", existingId).with(csrf()));

        result.andExpect(status().isNoContent());
    }
}
