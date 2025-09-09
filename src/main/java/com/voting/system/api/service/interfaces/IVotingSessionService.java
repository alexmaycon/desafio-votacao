package com.voting.system.api.service.interfaces;

import com.voting.system.api.model.dto.request.VotingSessionRequestDTO;
import com.voting.system.api.model.dto.response.VotingSessionResponseDTO;
import com.voting.system.api.model.dto.response.VotingResultDTO;
import com.voting.system.api.model.enums.VotingSessionStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IVotingSessionService {

    VotingSessionResponseDTO create(VotingSessionRequestDTO requestDTO);
    
    VotingSessionResponseDTO findById(Long id);
    
    Page<VotingSessionResponseDTO> findAll(Pageable pageable);
    
    Page<VotingSessionResponseDTO> findByStatus(VotingSessionStatusEnum status, Pageable pageable);
    
    Page<VotingSessionResponseDTO> findByAgendaId(Long agendaId, Pageable pageable);
    
    VotingSessionResponseDTO start(Long id);
    
    VotingSessionResponseDTO close(Long id);
    
    VotingResultDTO getResult(Long id);
    
    void checkExpiredSessions();
}
