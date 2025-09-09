package com.voting.system.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.system.api.model.dto.request.AssociateRequestDTO;
import com.voting.system.api.model.dto.response.AssociateResponseDTO;
import com.voting.system.api.service.AssociateService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssociateController.class)
class AssociateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssociateService associateService;

    @Autowired
    private ObjectMapper objectMapper;

    private AssociateRequestDTO associateRequestDTO;
    private AssociateResponseDTO associateResponseDTO;

    @BeforeEach
    void setUp() {
        associateRequestDTO = new AssociateRequestDTO();
        associateRequestDTO.setName("John Doe");
        associateRequestDTO.setCpf("12345678901");

        associateResponseDTO = new AssociateResponseDTO();
        associateResponseDTO.setId(1L);
        associateResponseDTO.setName("John Doe");
        associateResponseDTO.setCpf("12345678901");
        associateResponseDTO.setIsActive(true);
        associateResponseDTO.setDtCreated(OffsetDateTime.now());
    }

    @Test
    void create_ShouldCreateAssociate_WhenValidRequest() throws Exception {
        when(associateService.create(any(AssociateRequestDTO.class))).thenReturn(associateResponseDTO);

        mockMvc.perform(post("/api/v1/associates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associateRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.cpf").value("12345678901"));

        verify(associateService).create(any(AssociateRequestDTO.class));
    }

    @Test
    void findById_ShouldReturnAssociate_WhenAssociateExists() throws Exception {
        when(associateService.findById(1L)).thenReturn(associateResponseDTO);

        mockMvc.perform(get("/api/v1/associates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.cpf").value("12345678901"));

        verify(associateService).findById(1L);
    }

    @Test
    void findByCpf_ShouldReturnAssociate_WhenAssociateExists() throws Exception {
        when(associateService.findByCpf("12345678901")).thenReturn(associateResponseDTO);

        mockMvc.perform(get("/api/v1/associates/cpf/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.cpf").value("12345678901"));

        verify(associateService).findByCpf("12345678901");
    }

    @Test
    void findAll_ShouldReturnPageOfAssociates() throws Exception {
        Page<AssociateResponseDTO> associatePage = new PageImpl<>(Arrays.asList(associateResponseDTO));
        when(associateService.findAll(any())).thenReturn(associatePage);

        mockMvc.perform(get("/api/v1/associates")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(associateService).findAll(any());
    }

    @Test
    void update_ShouldUpdateAssociate_WhenValidRequest() throws Exception {
        associateRequestDTO.setId(1L);
        when(associateService.update(eq(1L), any(AssociateRequestDTO.class))).thenReturn(associateResponseDTO);

        mockMvc.perform(put("/api/v1/associates/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(associateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(associateService).update(eq(1L), any(AssociateRequestDTO.class));
    }

    @Test
    void activate_ShouldActivateAssociate() throws Exception {
        mockMvc.perform(patch("/api/v1/associates/1/activate"))
                .andExpect(status().isNoContent());

        verify(associateService).activate(1L);
    }

    @Test
    void deactivate_ShouldDeactivateAssociate() throws Exception {
        mockMvc.perform(patch("/api/v1/associates/1/deactivate"))
                .andExpect(status().isNoContent());

        verify(associateService).deactivate(1L);
    }
}
