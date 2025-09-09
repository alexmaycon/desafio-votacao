package com.voting.system.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.system.api.model.dto.request.AgendaRequestDTO;
import com.voting.system.api.model.dto.response.AgendaResponseDTO;
import com.voting.system.api.service.AgendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendaController.class)
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgendaService agendaService;

    @Autowired
    private ObjectMapper objectMapper;

    private AgendaRequestDTO agendaRequestDTO;
    private AgendaResponseDTO agendaResponseDTO;

    @BeforeEach
    void setUp() {
        agendaRequestDTO = new AgendaRequestDTO();
        agendaRequestDTO.setTitle("Test Agenda");
        agendaRequestDTO.setDescription("Test Description");

        agendaResponseDTO = new AgendaResponseDTO();
        agendaResponseDTO.setId(1L);
        agendaResponseDTO.setTitle("Test Agenda");
        agendaResponseDTO.setDescription("Test Description");
        agendaResponseDTO.setIsActive(true);
        agendaResponseDTO.setDtCreated(OffsetDateTime.now());
    }

    @Test
    void create_ShouldCreateAgenda_WhenValidRequest() throws Exception {
        when(agendaService.create(any(AgendaRequestDTO.class))).thenReturn(agendaResponseDTO);

        mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Agenda"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(agendaService).create(any(AgendaRequestDTO.class));
    }

    @Test
    void findById_ShouldReturnAgenda_WhenAgendaExists() throws Exception {
        when(agendaService.findById(1L)).thenReturn(agendaResponseDTO);

        mockMvc.perform(get("/api/v1/agendas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Agenda"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(agendaService).findById(1L);
    }

    @Test
    void findAll_ShouldReturnPageOfAgendas() throws Exception {
        Page<AgendaResponseDTO> agendaPage = new PageImpl<>(Arrays.asList(agendaResponseDTO));
        when(agendaService.findAll(any())).thenReturn(agendaPage);

        mockMvc.perform(get("/api/v1/agendas")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(agendaService).findAll(any());
    }

    @Test
    void update_ShouldUpdateAgenda_WhenValidRequest() throws Exception {
        agendaRequestDTO.setId(1L);
        when(agendaService.update(eq(1L), any(AgendaRequestDTO.class))).thenReturn(agendaResponseDTO);

        mockMvc.perform(put("/api/v1/agendas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Agenda"));

        verify(agendaService).update(eq(1L), any(AgendaRequestDTO.class));
    }

    @Test
    void activate_ShouldActivateAgenda() throws Exception {
        mockMvc.perform(patch("/api/v1/agendas/1/activate"))
                .andExpect(status().isNoContent());

        verify(agendaService).activate(1L);
    }

    @Test
    void deactivate_ShouldDeactivateAgenda() throws Exception {
        mockMvc.perform(patch("/api/v1/agendas/1/deactivate"))
                .andExpect(status().isNoContent());

        verify(agendaService).deactivate(1L);
    }
}
