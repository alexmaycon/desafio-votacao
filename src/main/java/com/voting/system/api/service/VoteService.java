package com.voting.system.api.service;

import com.voting.system.api.exception.ResourceNotFoundException;
import com.voting.system.api.exception.VoteException;
import com.voting.system.api.model.dto.request.VoteRequestDTO;
import com.voting.system.api.model.dto.response.VoteResponseDTO;
import com.voting.system.api.model.dto.response.VotingResultDTO;
import com.voting.system.api.model.entity.Associate;
import com.voting.system.api.model.entity.Vote;
import com.voting.system.api.model.entity.VoteValue;
import com.voting.system.api.model.entity.VotingSession;
import com.voting.system.api.model.enums.AssociateStatusEnum;
import com.voting.system.api.model.enums.VotingSessionStatusEnum;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.repository.AssociateRepository;
import com.voting.system.api.repository.VoteRepository;
import com.voting.system.api.repository.VotingSessionRepository;
import com.voting.system.api.service.interfaces.IVoteService;
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
public class VoteService implements IVoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final AssociateRepository associateRepository;
    private final GenericValidator genericValidator;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public VoteResponseDTO vote(VoteRequestDTO requestDTO) {
        genericValidator.validate(requestDTO, ICreateValidationGroup.class);
        
        VotingSession votingSession = votingSessionRepository.findById(requestDTO.getVotingSessionId())
            .orElseThrow(() -> new ResourceNotFoundException("Sessão de Votação", requestDTO.getVotingSessionId()));
        
        if (votingSession.getStatus() != VotingSessionStatusEnum.ACTIVE) {
            throw new VoteException("Só é possível votar em sessões ativas");
        }
        
        OffsetDateTime now = OffsetDateTime.now();
        if (votingSession.getEndTime() != null && now.isAfter(votingSession.getEndTime())) {
            throw new VoteException("Sessão de votação expirada");
        }
        
        Associate associate = associateRepository.findByIdAndIsActiveTrue(requestDTO.getAssociateId())
            .orElseThrow(() -> new ResourceNotFoundException("Associado", requestDTO.getAssociateId()));
        
        if (associate.getStatus() == AssociateStatusEnum.UNABLE_TO_VOTE) {
            throw new VoteException("Associado não habilitado para votar no momento");
        }
        
        if (voteRepository.existsByVotingSessionIdAndAssociateId(requestDTO.getVotingSessionId(), requestDTO.getAssociateId())) {
            throw new VoteException("Associado já votou nesta sessão");
        }
        
        Vote vote = new Vote();
        vote.setVotingSession(votingSession);
        vote.setAssociate(associate);
        vote.setValue(requestDTO.getValue());
        vote.setVoteTime(now);
        
        Vote savedVote = voteRepository.save(vote);
        
        return mapToResponseDTO(savedVote);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoteResponseDTO> findByVotingSessionId(Long votingSessionId, Pageable pageable) {
        Page<Vote> votes = voteRepository.findByVotingSessionIdOrderByVoteTimeDesc(votingSessionId, pageable);
        return votes.map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public VotingResultDTO getVotingResult(Long votingSessionId) {
        VotingSession session = votingSessionRepository.findById(votingSessionId)
            .orElseThrow(() -> new ResourceNotFoundException("Sessão de Votação", votingSessionId));
        
        Long totalVotes = voteRepository.countByVotingSessionId(votingSessionId);
        Long yesVotes = voteRepository.countByVotingSessionIdAndValue(votingSessionId, VoteValue.YES);
        Long noVotes = voteRepository.countByVotingSessionIdAndValue(votingSessionId, VoteValue.NO);
        
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
    @Transactional(readOnly = true)
    public boolean hasAssociateVoted(Long votingSessionId, Long associateId) {
        return voteRepository.existsByVotingSessionIdAndAssociateId(votingSessionId, associateId);
    }
    
    private VoteResponseDTO mapToResponseDTO(Vote vote) {
        VoteResponseDTO responseDTO = modelMapper.map(vote, VoteResponseDTO.class);
        responseDTO.setVotingSessionId(vote.getVotingSession().getId());
        responseDTO.setAssociateId(vote.getAssociate().getId());
        responseDTO.setAssociateName(vote.getAssociate().getName());
        return responseDTO;
    }
}
