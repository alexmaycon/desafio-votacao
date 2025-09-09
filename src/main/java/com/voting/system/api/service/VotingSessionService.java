package com.voting.system.api.service;

import com.voting.system.api.exception.ResourceNotFoundException;
import com.voting.system.api.exception.VotingSessionException;
import com.voting.system.api.model.dto.request.VotingSessionRequestDTO;
import com.voting.system.api.model.dto.response.VotingResultDTO;
import com.voting.system.api.model.dto.response.VotingSessionResponseDTO;
import com.voting.system.api.model.entity.Agenda;
import com.voting.system.api.model.entity.VotingSession;
import com.voting.system.api.model.enums.VotingSessionStatusEnum;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.repository.AgendaRepository;
import com.voting.system.api.repository.VotingSessionRepository;
import com.voting.system.api.repository.VoteRepository;
import com.voting.system.api.service.interfaces.IVotingSessionService;
import com.voting.system.api.service.validator.GenericValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class VotingSessionService implements IVotingSessionService {

    private final VotingSessionRepository votingSessionRepository;
    private final AgendaRepository agendaRepository;
    private final VoteRepository voteRepository;
    private final GenericValidator genericValidator;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public VotingSessionResponseDTO create(VotingSessionRequestDTO requestDTO) {
        genericValidator.validate(requestDTO, ICreateValidationGroup.class);
        
        Agenda agenda = agendaRepository.findByIdAndIsActiveTrue(requestDTO.getAgendaId())
            .orElseThrow(() -> new ResourceNotFoundException("Agenda", requestDTO.getAgendaId()));
        
        if (votingSessionRepository.existsByAgendaIdAndStatusIn(requestDTO.getAgendaId(), 
            VotingSessionStatusEnum.PENDING, VotingSessionStatusEnum.ACTIVE)) {
            throw new VotingSessionException("Já existe uma sessão de votação ativa ou pendente para esta pauta");
        }
        
        VotingSession votingSession = new VotingSession();
        votingSession.setAgenda(agenda);
        votingSession.setStatus(VotingSessionStatusEnum.PENDING);
        votingSession.setDurationMinutes(requestDTO.getDurationMinutes() != null ? 
            requestDTO.getDurationMinutes() : 1);
        
        VotingSession savedSession = votingSessionRepository.save(votingSession);
        
        VotingSessionResponseDTO responseDTO = modelMapper.map(savedSession, VotingSessionResponseDTO.class);
        responseDTO.setAgendaTitle(agenda.getTitle());
        return responseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public VotingSessionResponseDTO findById(Long id) {
        VotingSession session = votingSessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sessão de Votação", id));
        
        VotingSessionResponseDTO responseDTO = modelMapper.map(session, VotingSessionResponseDTO.class);
        responseDTO.setAgendaTitle(session.getAgenda().getTitle());
        return responseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VotingSessionResponseDTO> findAll(Pageable pageable) {
        Page<VotingSession> sessions = votingSessionRepository.findAll(pageable);
        return sessions.map(session -> {
            VotingSessionResponseDTO dto = modelMapper.map(session, VotingSessionResponseDTO.class);
            dto.setAgendaTitle(session.getAgenda().getTitle());
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VotingSessionResponseDTO> findByStatus(VotingSessionStatusEnum status, Pageable pageable) {
        Page<VotingSession> sessions = votingSessionRepository.findByStatus(status, pageable);
        return sessions.map(session -> {
            VotingSessionResponseDTO dto = modelMapper.map(session, VotingSessionResponseDTO.class);
            dto.setAgendaTitle(session.getAgenda().getTitle());
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VotingSessionResponseDTO> findByAgendaId(Long agendaId, Pageable pageable) {
        Page<VotingSession> sessions = votingSessionRepository.findByAgendaId(agendaId, pageable);
        return sessions.map(session -> {
            VotingSessionResponseDTO dto = modelMapper.map(session, VotingSessionResponseDTO.class);
            dto.setAgendaTitle(session.getAgenda().getTitle());
            return dto;
        });
    }

    @Override
    @Transactional
    public VotingSessionResponseDTO start(Long id) {
        VotingSession session = votingSessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sessão de Votação", id));
        
        if (session.getStatus() != VotingSessionStatusEnum.PENDING) {
            throw new VotingSessionException("Só é possível iniciar sessões com status PENDING");
        }
        
        OffsetDateTime now = OffsetDateTime.now();
        session.setStatus(VotingSessionStatusEnum.ACTIVE);
        session.setStartTime(now);
        session.setEndTime(now.plusMinutes(session.getDurationMinutes()));
        
        VotingSession savedSession = votingSessionRepository.save(session);
        
        VotingSessionResponseDTO responseDTO = modelMapper.map(savedSession, VotingSessionResponseDTO.class);
        responseDTO.setAgendaTitle(savedSession.getAgenda().getTitle());
        return responseDTO;
    }

    @Override
    @Transactional
    public VotingSessionResponseDTO close(Long id) {
        VotingSession session = votingSessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sessão de Votação", id));
        
        if (session.getStatus() != VotingSessionStatusEnum.ACTIVE) {
            throw new VotingSessionException("Só é possível fechar sessões com status ACTIVE");
        }
        
        session.setStatus(VotingSessionStatusEnum.CLOSED);
        VotingSession savedSession = votingSessionRepository.save(session);
        
        VotingSessionResponseDTO responseDTO = modelMapper.map(savedSession, VotingSessionResponseDTO.class);
        responseDTO.setAgendaTitle(savedSession.getAgenda().getTitle());
        return responseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public VotingResultDTO getResult(Long id) {
        VotingSession session = votingSessionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Sessão de Votação", id));
        
        if (session.getStatus() == VotingSessionStatusEnum.PENDING) {
            throw new VotingSessionException("Não é possível obter resultado de sessão que ainda não foi iniciada");
        }
        
        Long totalVotes = voteRepository.countByVotingSessionId(id);
        Long yesVotes = voteRepository.countByVotingSessionIdAndValue(id, com.voting.system.api.model.entity.VoteValue.YES);
        Long noVotes = voteRepository.countByVotingSessionIdAndValue(id, com.voting.system.api.model.entity.VoteValue.NO);
        
        String result = yesVotes > noVotes ? "APROVADA" : 
                       noVotes > yesVotes ? "REJEITADA" : "EMPATE";
        
        return new VotingResultDTO(
            session.getAgenda().getId(),
            session.getAgenda().getTitle(),
            session.getId(),
            totalVotes,
            yesVotes,
            noVotes,
            result
        );
    }

    @Override
    @Transactional
    public void checkExpiredSessions() {
        OffsetDateTime now = OffsetDateTime.now();
        votingSessionRepository.findExpiredSessions(now)
            .forEach(session -> {
                session.setStatus(VotingSessionStatusEnum.CLOSED);
                votingSessionRepository.save(session);
            });
    }

    @Transactional
    public int closeExpiredSessions() {
        OffsetDateTime now = OffsetDateTime.now();
        return votingSessionRepository.findExpiredSessions(now)
            .stream()
            .mapToInt(session -> {
                session.setStatus(VotingSessionStatusEnum.CLOSED);
                votingSessionRepository.save(session);
                return 1;
            })
            .sum();
    }
}
