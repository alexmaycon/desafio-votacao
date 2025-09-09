package com.voting.system.api.service;


import com.voting.system.api.exception.ResourceNotFoundException;
import com.voting.system.api.model.dto.request.VotingSessionRequestDTO;
import com.voting.system.api.model.dto.response.VotingSessionResponseDTO;
import com.voting.system.api.model.dto.response.VotingResultDTO;
import com.voting.system.api.model.entity.Agenda;
import com.voting.system.api.model.entity.VotingSession;
import com.voting.system.api.model.enums.VotingSessionStatusEnum;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.repository.VotingSessionRepository;
import com.voting.system.api.repository.VoteRepository;
import com.voting.system.api.repository.AgendaRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Arrays;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingSessionServiceTest {

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private GenericValidator genericValidator;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private VotingSessionService votingSessionService;

    private VotingSession votingSession;
    private VotingSessionRequestDTO votingSessionRequestDTO;
    private VotingSessionResponseDTO votingSessionResponseDTO;
    private Agenda agenda;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        agenda = new Agenda();
        agenda.setId(1L);
        agenda.setTitle("Test Agenda");

        votingSession = new VotingSession();
        votingSession.setId(1L);
        votingSession.setAgenda(agenda);
        votingSession.setStartTime(OffsetDateTime.now());
        votingSession.setEndTime(OffsetDateTime.now().plusMinutes(1));
        votingSession.setStatus(VotingSessionStatusEnum.ACTIVE);
        votingSession.setDurationMinutes(1);
        votingSession.setDtCreated(OffsetDateTime.now());

        votingSessionRequestDTO = new VotingSessionRequestDTO();
        votingSessionRequestDTO.setAgendaId(1L);
        votingSessionRequestDTO.setDurationMinutes(1);

        votingSessionResponseDTO = new VotingSessionResponseDTO();
        votingSessionResponseDTO.setId(1L);
        votingSessionResponseDTO.setAgendaId(1L);
        votingSessionResponseDTO.setStartTime(votingSession.getStartTime());
        votingSessionResponseDTO.setEndTime(votingSession.getEndTime());

        pageable = PageRequest.of(0, 20);
    }

    @Test
    void create_ShouldCreateVotingSessionSuccessfully() {
        doNothing().when(genericValidator).validate(votingSessionRequestDTO, ICreateValidationGroup.class);
        when(agendaRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(agenda));
        when(votingSessionRepository.existsByAgendaIdAndStatusIn(1L, VotingSessionStatusEnum.PENDING, VotingSessionStatusEnum.ACTIVE)).thenReturn(false);
        when(votingSessionRepository.save(any(VotingSession.class))).thenReturn(votingSession);
        when(modelMapper.map(votingSession, VotingSessionResponseDTO.class)).thenReturn(votingSessionResponseDTO);

        VotingSessionResponseDTO result = votingSessionService.create(votingSessionRequestDTO);

        assertNotNull(result);
        assertEquals(votingSessionResponseDTO.getId(), result.getId());
        assertEquals(votingSessionResponseDTO.getAgendaId(), result.getAgendaId());
        verify(genericValidator).validate(votingSessionRequestDTO, ICreateValidationGroup.class);
        verify(agendaRepository).findByIdAndIsActiveTrue(1L);
        verify(votingSessionRepository).existsByAgendaIdAndStatusIn(1L, VotingSessionStatusEnum.PENDING, VotingSessionStatusEnum.ACTIVE);
        verify(votingSessionRepository).save(any(VotingSession.class));
    }

    @Test
    void create_ShouldThrowVotingSessionException_WhenActiveSessionAlreadyExists() {
        doNothing().when(genericValidator).validate(votingSessionRequestDTO, ICreateValidationGroup.class);
        when(agendaRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(agenda));
        when(votingSessionRepository.existsByAgendaIdAndStatusIn(1L, VotingSessionStatusEnum.PENDING, VotingSessionStatusEnum.ACTIVE)).thenReturn(true);

        assertThrows(Exception.class, () -> votingSessionService.create(votingSessionRequestDTO));

        verify(genericValidator).validate(votingSessionRequestDTO, ICreateValidationGroup.class);
        verify(agendaRepository).findByIdAndIsActiveTrue(1L);
        verify(votingSessionRepository).existsByAgendaIdAndStatusIn(1L, VotingSessionStatusEnum.PENDING, VotingSessionStatusEnum.ACTIVE);
        verify(votingSessionRepository, never()).save(any());
    }

    @Test
    void findById_ShouldReturnVotingSession_WhenVotingSessionExists() {
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.of(votingSession));
        when(modelMapper.map(votingSession, VotingSessionResponseDTO.class)).thenReturn(votingSessionResponseDTO);

        VotingSessionResponseDTO result = votingSessionService.findById(1L);

        assertNotNull(result);
        assertEquals(votingSessionResponseDTO.getId(), result.getId());
        assertEquals(votingSessionResponseDTO.getAgendaId(), result.getAgendaId());
        verify(votingSessionRepository).findById(1L);
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenVotingSessionNotExists() {
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> votingSessionService.findById(1L));

        verify(votingSessionRepository).findById(1L);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void findAll_ShouldReturnPageOfVotingSessions() {
        Page<VotingSession> votingSessionPage = new PageImpl<>(Arrays.asList(votingSession));
        when(votingSessionRepository.findAll(pageable)).thenReturn(votingSessionPage);
        when(modelMapper.map(votingSession, VotingSessionResponseDTO.class)).thenReturn(votingSessionResponseDTO);

        Page<VotingSessionResponseDTO> result = votingSessionService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(votingSessionResponseDTO.getId(), result.getContent().get(0).getId());
        verify(votingSessionRepository).findAll(pageable);
    }

    @Test
    void findByAgendaId_ShouldReturnVotingSessions() {
        Page<VotingSession> votingSessionPage = new PageImpl<>(Arrays.asList(votingSession));
        when(votingSessionRepository.findByAgendaId(1L, pageable)).thenReturn(votingSessionPage);
        when(modelMapper.map(votingSession, VotingSessionResponseDTO.class)).thenReturn(votingSessionResponseDTO);

        Page<VotingSessionResponseDTO> result = votingSessionService.findByAgendaId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(votingSessionResponseDTO.getId(), result.getContent().get(0).getId());
        verify(votingSessionRepository).findByAgendaId(1L, pageable);
    }

    @Test
    void findByStatus_ShouldReturnVotingSessions() {
        Page<VotingSession> votingSessionPage = new PageImpl<>(Arrays.asList(votingSession));
        when(votingSessionRepository.findByStatus(VotingSessionStatusEnum.ACTIVE, pageable)).thenReturn(votingSessionPage);
        when(modelMapper.map(votingSession, VotingSessionResponseDTO.class)).thenReturn(votingSessionResponseDTO);

        Page<VotingSessionResponseDTO> result = votingSessionService.findByStatus(VotingSessionStatusEnum.ACTIVE, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(votingSessionResponseDTO.getId(), result.getContent().get(0).getId());
        verify(votingSessionRepository).findByStatus(VotingSessionStatusEnum.ACTIVE, pageable);
    }

    @Test
    void start_ShouldStartSession() {
        votingSession.setStatus(VotingSessionStatusEnum.PENDING);
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.of(votingSession));
        when(votingSessionRepository.save(any(VotingSession.class))).thenReturn(votingSession);
        when(modelMapper.map(votingSession, VotingSessionResponseDTO.class)).thenReturn(votingSessionResponseDTO);

        VotingSessionResponseDTO result = votingSessionService.start(1L);

        assertNotNull(result);
        assertEquals(VotingSessionStatusEnum.ACTIVE, votingSession.getStatus());
        assertNotNull(votingSession.getStartTime());
        assertNotNull(votingSession.getEndTime());
        verify(votingSessionRepository).findById(1L);
        verify(votingSessionRepository).save(votingSession);
    }

    @Test
    void close_ShouldCloseSession() {
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.of(votingSession));
        when(votingSessionRepository.save(any(VotingSession.class))).thenReturn(votingSession);
        when(modelMapper.map(votingSession, VotingSessionResponseDTO.class)).thenReturn(votingSessionResponseDTO);

        VotingSessionResponseDTO result = votingSessionService.close(1L);

        assertNotNull(result);
        assertEquals(VotingSessionStatusEnum.CLOSED, votingSession.getStatus());
        verify(votingSessionRepository).findById(1L);
        verify(votingSessionRepository).save(votingSession);
    }

    @Test
    void closeExpiredSessions_ShouldCloseExpiredSessions() {
        when(votingSessionRepository.findExpiredSessions(any(OffsetDateTime.class)))
                .thenReturn(Arrays.asList(votingSession));

        int result = votingSessionService.closeExpiredSessions();

        assertEquals(1, result);
        assertEquals(VotingSessionStatusEnum.CLOSED, votingSession.getStatus());
        verify(votingSessionRepository).findExpiredSessions(any(OffsetDateTime.class));
        verify(votingSessionRepository).save(votingSession);
    }

    @Test
    void checkExpiredSessions_ShouldCallCheckMethod() {
        votingSessionService.checkExpiredSessions();
        
        verify(votingSessionRepository).findExpiredSessions(any(OffsetDateTime.class));
    }

    @Test
    void getResult_ShouldReturnVotingResult() {
        when(votingSessionRepository.findById(1L)).thenReturn(Optional.of(votingSession));
        when(voteRepository.countByVotingSessionId(1L)).thenReturn(10L);
        when(voteRepository.countByVotingSessionIdAndValue(eq(1L), any())).thenReturn(7L, 3L);

        VotingResultDTO result = votingSessionService.getResult(1L);

        assertNotNull(result);
        assertEquals(1L, result.getAgendaId());
        assertEquals(1L, result.getVotingSessionId());
        assertEquals(10L, result.getTotalVotes());
        verify(votingSessionRepository).findById(1L);
        verify(voteRepository).countByVotingSessionId(1L);
    }
}
