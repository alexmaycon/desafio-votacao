package com.voting.system.api.service;

import com.voting.system.api.exception.ResourceNotFoundException;
import com.voting.system.api.exception.VoteException;
import com.voting.system.api.model.dto.request.VoteRequestDTO;
import com.voting.system.api.model.dto.response.VoteResponseDTO;
import com.voting.system.api.model.dto.response.VotingResultDTO;
import com.voting.system.api.model.entity.*;
import com.voting.system.api.model.enums.VotingSessionStatusEnum;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.repository.AssociateRepository;
import com.voting.system.api.repository.VoteRepository;
import com.voting.system.api.repository.VotingSessionRepository;
import com.voting.system.api.service.validator.GenericValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private AssociateRepository associateRepository;

    @Mock
    private GenericValidator genericValidator;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private VoteService voteService;

    private VoteRequestDTO voteRequestDTO;
    private VoteResponseDTO voteResponseDTO;
    private Vote vote;
    private VotingSession votingSession;
    private Associate associate;
    private Agenda agenda;

    @BeforeEach
    void setUp() {
        agenda = new Agenda();
        agenda.setId(1L);
        agenda.setTitle("Test Agenda");

        votingSession = new VotingSession();
        votingSession.setId(1L);
        votingSession.setAgenda(agenda);
        votingSession.setStatus(VotingSessionStatusEnum.ACTIVE);
        votingSession.setStartTime(OffsetDateTime.now().minusMinutes(10));
        votingSession.setEndTime(OffsetDateTime.now().plusMinutes(10));

        associate = new Associate();
        associate.setId(1L);
        associate.setName("Test Associate");
        associate.setIsActive(true);

        vote = new Vote();
        vote.setId(1L);
        vote.setVotingSession(votingSession);
        vote.setAssociate(associate);
        vote.setValue(VoteValue.YES);
        vote.setVoteTime(OffsetDateTime.now());

        voteRequestDTO = new VoteRequestDTO();
        voteRequestDTO.setVotingSessionId(1L);
        voteRequestDTO.setAssociateId(1L);
        voteRequestDTO.setValue(VoteValue.YES);

        voteResponseDTO = new VoteResponseDTO();
        voteResponseDTO.setId(1L);
        voteResponseDTO.setVotingSessionId(1L);
        voteResponseDTO.setAssociateId(1L);
        voteResponseDTO.setAssociateName("Test Associate");
        voteResponseDTO.setValue(VoteValue.YES);
    }

    @Test
    void vote_ShouldThrowException_WhenVotingSessionNotFound() {
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> voteService.vote(voteRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sessão de Votação");

        verify(voteRepository, never()).save(any());
    }

    @Test
    void vote_ShouldThrowException_WhenAssociateNotFound() {
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.of(votingSession));
        when(associateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> voteService.vote(voteRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Associado");

        verify(voteRepository, never()).save(any());
    }

    @Test
    void findByVotingSessionId_ShouldReturnPageOfVotes() {
        Page<Vote> votePage = new PageImpl<>(Arrays.asList(vote));
        when(voteRepository.findByVotingSessionIdOrderByVoteTimeDesc(eq(1L), any(Pageable.class)))
                .thenReturn(votePage);
        when(modelMapper.map(vote, VoteResponseDTO.class)).thenReturn(voteResponseDTO);

        Page<VoteResponseDTO> result = voteService.findByVotingSessionId(1L, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        verify(voteRepository).findByVotingSessionIdOrderByVoteTimeDesc(eq(1L), any(Pageable.class));
    }

    @Test
    void getVotingResult_ShouldReturnResult() {
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.of(votingSession));
        when(voteRepository.countByVotingSessionId(1L)).thenReturn(10L);
        when(voteRepository.countByVotingSessionIdAndValue(1L, VoteValue.YES)).thenReturn(7L);
        when(voteRepository.countByVotingSessionIdAndValue(1L, VoteValue.NO)).thenReturn(3L);

        VotingResultDTO result = voteService.getVotingResult(1L);

        assertThat(result).isNotNull();
        assertThat(result.getAgendaId()).isEqualTo(1L);
        assertThat(result.getTotalVotes()).isEqualTo(10L);
        assertThat(result.getYesVotes()).isEqualTo(7L);
        assertThat(result.getNoVotes()).isEqualTo(3L);
        assertThat(result.getResult()).isEqualTo("APROVADA");
    }

    @Test
    void hasAssociateVoted_ShouldReturnTrue_WhenAssociateVoted() {
        when(voteRepository.existsByVotingSessionIdAndAssociateId(1L, 1L)).thenReturn(true);

        boolean result = voteService.hasAssociateVoted(1L, 1L);

        assertThat(result).isTrue();
        verify(voteRepository).existsByVotingSessionIdAndAssociateId(1L, 1L);
    }

    @Test
    void hasAssociateVoted_ShouldReturnFalse_WhenAssociateNotVoted() {
        when(voteRepository.existsByVotingSessionIdAndAssociateId(1L, 1L)).thenReturn(false);

        boolean result = voteService.hasAssociateVoted(1L, 1L);

        assertThat(result).isFalse();
        verify(voteRepository).existsByVotingSessionIdAndAssociateId(1L, 1L);
    }
}
