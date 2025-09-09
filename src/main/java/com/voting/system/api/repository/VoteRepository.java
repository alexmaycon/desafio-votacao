package com.voting.system.api.repository;

import com.voting.system.api.model.entity.Vote;
import com.voting.system.api.model.entity.VoteValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByVotingSessionId(Long votingSessionId);
    
    Page<Vote> findByVotingSessionIdOrderByVoteTimeDesc(Long votingSessionId, Pageable pageable);

    List<Vote> findByAssociateId(Long associateId);

    Optional<Vote> findByVotingSessionIdAndAssociateId(Long votingSessionId, Long associateId);

    boolean existsByVotingSessionIdAndAssociateId(Long votingSessionId, Long associateId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.votingSession.id = :votingSessionId")
    long countByVotingSessionId(@Param("votingSessionId") Long votingSessionId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.votingSession.id = :votingSessionId AND v.value = :value")
    long countByVotingSessionIdAndValue(@Param("votingSessionId") Long votingSessionId, @Param("value") VoteValue value);

    @Query("SELECT v.value, COUNT(v) FROM Vote v WHERE v.votingSession.id = :votingSessionId GROUP BY v.value")
    List<Object[]> countVotesByValueForSession(@Param("votingSessionId") Long votingSessionId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.associate.id = :associateId")
    long countByAssociateId(@Param("associateId") Long associateId);

    @Query("SELECT v FROM Vote v WHERE v.votingSession.agenda.id = :agendaId")
    List<Vote> findByAgendaId(@Param("agendaId") Long agendaId);
}
