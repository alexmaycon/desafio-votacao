package com.voting.system.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.system.api.model.dto.request.VotingSessionRequestDTO;
import com.voting.system.api.model.dto.response.VotingResultDTO;
import com.voting.system.api.model.dto.response.VotingSessionResponseDTO;
import com.voting.system.api.model.enums.VotingSessionStatusEnum;
import com.voting.system.api.service.VotingSessionService;
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

@WebMvcTest(VotingSessionController.class)
class VotingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VotingSessionService votingSessionService;

    @Autowired
    private ObjectMapper objectMapper;

    private VotingSessionRequestDTO votingSessionRequestDTO;
    private VotingSessionResponseDTO votingSessionResponseDTO;
    private VotingResultDTO votingResultDTO;

    @BeforeEach
    void setUp() {
        votingSessionRequestDTO = new VotingSessionRequestDTO();
        votingSessionRequestDTO.setAgendaId(1L);
        votingSessionRequestDTO.setDurationMinutes(1);

        votingSessionResponseDTO = new VotingSessionResponseDTO();
        votingSessionResponseDTO.setId(1L);
        votingSessionResponseDTO.setAgendaId(1L);
        votingSessionResponseDTO.setAgendaTitle("Test Agenda");
        votingSessionResponseDTO.setStatus(VotingSessionStatusEnum.ACTIVE);
        votingSessionResponseDTO.setStartTime(OffsetDateTime.now());
        votingSessionResponseDTO.setEndTime(OffsetDateTime.now().plusMinutes(1));
        votingSessionResponseDTO.setDurationMinutes(1);

        votingResultDTO = new VotingResultDTO(
            1L, "Test Agenda", 1L, 10L, 7L, 3L, "APROVADA"
        );
    }

    @Test
    void create_ShouldCreateVotingSession_WhenValidRequest() throws Exception {
        when(votingSessionService.create(any(VotingSessionRequestDTO.class))).thenReturn(votingSessionResponseDTO);

        mockMvc.perform(post("/api/v1/voting-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(votingSessionRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.agendaId").value(1L))
                .andExpect(jsonPath("$.agendaTitle").value("Test Agenda"));

        verify(votingSessionService).create(any(VotingSessionRequestDTO.class));
    }

    @Test
    void findById_ShouldReturnVotingSession_WhenSessionExists() throws Exception {
        when(votingSessionService.findById(1L)).thenReturn(votingSessionResponseDTO);

        mockMvc.perform(get("/api/v1/voting-sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.agendaId").value(1L))
                .andExpect(jsonPath("$.agendaTitle").value("Test Agenda"));

        verify(votingSessionService).findById(1L);
    }

    @Test
    void findAll_ShouldReturnPageOfVotingSessions() throws Exception {
        Page<VotingSessionResponseDTO> sessionPage = new PageImpl<>(Arrays.asList(votingSessionResponseDTO));
        when(votingSessionService.findAll(any())).thenReturn(sessionPage);

        mockMvc.perform(get("/api/v1/voting-sessions")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(votingSessionService).findAll(any());
    }

    @Test
    void findByStatus_ShouldReturnPageOfVotingSessions() throws Exception {
        Page<VotingSessionResponseDTO> sessionPage = new PageImpl<>(Arrays.asList(votingSessionResponseDTO));
        when(votingSessionService.findByStatus(eq(VotingSessionStatusEnum.ACTIVE), any())).thenReturn(sessionPage);

        mockMvc.perform(get("/api/v1/voting-sessions/status/ACTIVE")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(votingSessionService).findByStatus(eq(VotingSessionStatusEnum.ACTIVE), any());
    }

    @Test
    void start_ShouldStartVotingSession() throws Exception {
        when(votingSessionService.start(1L)).thenReturn(votingSessionResponseDTO);

        mockMvc.perform(patch("/api/v1/voting-sessions/1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(votingSessionService).start(1L);
    }

    @Test
    void close_ShouldCloseVotingSession() throws Exception {
        votingSessionResponseDTO.setStatus(VotingSessionStatusEnum.CLOSED);
        when(votingSessionService.close(1L)).thenReturn(votingSessionResponseDTO);

        mockMvc.perform(patch("/api/v1/voting-sessions/1/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CLOSED"));

        verify(votingSessionService).close(1L);
    }

    @Test
    void getResult_ShouldReturnVotingResult() throws Exception {
        when(votingSessionService.getResult(1L)).thenReturn(votingResultDTO);

        mockMvc.perform(get("/api/v1/voting-sessions/1/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agendaId").value(1L))
                .andExpect(jsonPath("$.agendaTitle").value("Test Agenda"))
                .andExpect(jsonPath("$.votingSessionId").value(1L))
                .andExpect(jsonPath("$.totalVotes").value(10))
                .andExpect(jsonPath("$.yesVotes").value(7))
                .andExpect(jsonPath("$.noVotes").value(3))
                .andExpect(jsonPath("$.result").value("APROVADA"));

        verify(votingSessionService).getResult(1L);
    }
}
