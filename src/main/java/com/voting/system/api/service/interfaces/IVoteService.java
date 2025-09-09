package com.voting.system.api.service.interfaces;

import com.voting.system.api.model.dto.request.VoteRequestDTO;
import com.voting.system.api.model.dto.response.VoteResponseDTO;
import com.voting.system.api.model.dto.response.VotingResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IVoteService {

    VoteResponseDTO vote(VoteRequestDTO requestDTO);
    
    Page<VoteResponseDTO> findByVotingSessionId(Long votingSessionId, Pageable pageable);
    
    VotingResultDTO getVotingResult(Long votingSessionId);
    
    boolean hasAssociateVoted(Long votingSessionId, Long associateId);
}
