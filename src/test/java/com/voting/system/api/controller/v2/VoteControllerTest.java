package com.voting.system.api.controller.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.system.api.controller.VoteController;
import com.voting.system.api.model.dto.request.VoteRequestDTO;
import com.voting.system.api.model.dto.response.VoteResponseDTO;
import com.voting.system.api.model.dto.response.VotingResultDTO;
import com.voting.system.api.model.entity.VoteValue;
import com.voting.system.api.service.interfaces.IVoteService;
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

@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IVoteService voteService;

    @Autowired
    private ObjectMapper objectMapper;

    private VoteRequestDTO voteRequestDTO;
    private VoteResponseDTO voteResponseDTO;
    private VotingResultDTO votingResultDTO;

    @BeforeEach
    void setUp() {
        voteRequestDTO = new VoteRequestDTO();
        voteRequestDTO.setAssociateId(1L);
        voteRequestDTO.setVotingSessionId(1L);
        voteRequestDTO.setValue(VoteValue.YES);

        voteResponseDTO = new VoteResponseDTO();
        voteResponseDTO.setId(1L);
        voteResponseDTO.setAssociateId(1L);
        voteResponseDTO.setVotingSessionId(1L);
        voteResponseDTO.setValue(VoteValue.YES);
        voteResponseDTO.setVoteTime(OffsetDateTime.now());

        votingResultDTO = new VotingResultDTO(1L, "Test Agenda", 1L, 10L, 7L, 3L, "APROVADA");
    }

    @Test
    void vote_ShouldCreateVote_WhenValidRequest() throws Exception {
        when(voteService.vote(any(VoteRequestDTO.class))).thenReturn(voteResponseDTO);

        mockMvc.perform(post("/api/v1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.associateId").value(1L))
                .andExpect(jsonPath("$.votingSessionId").value(1L));

        verify(voteService).vote(any(VoteRequestDTO.class));
    }

    @Test
    void findByVotingSessionId_ShouldReturnPageOfVotes() throws Exception {
        Page<VoteResponseDTO> votePage = new PageImpl<>(Arrays.asList(voteResponseDTO));
        when(voteService.findByVotingSessionId(eq(1L), any())).thenReturn(votePage);

        mockMvc.perform(get("/api/v1/votes/session/1")
                .param("page", "0")
                .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(voteService).findByVotingSessionId(eq(1L), any());
    }

    @Test
    void getVotingResult_ShouldReturnVotingResult() throws Exception {
        when(voteService.getVotingResult(1L)).thenReturn(votingResultDTO);

        mockMvc.perform(get("/api/v1/votes/session/1/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agendaId").value(1L))
                .andExpect(jsonPath("$.votingSessionId").value(1L))
                .andExpect(jsonPath("$.totalVotes").value(10L))
                .andExpect(jsonPath("$.result").value("APROVADA"));

        verify(voteService).getVotingResult(1L);
    }

    @Test
    void hasAssociateVoted_ShouldReturnTrue_WhenAssociateHasVoted() throws Exception {
        when(voteService.hasAssociateVoted(1L, 1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/votes/associate/1/voted/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(voteService).hasAssociateVoted(1L, 1L);
    }
}
