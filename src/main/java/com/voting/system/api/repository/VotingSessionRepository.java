package com.voting.system.api.repository;

import com.voting.system.api.model.entity.VotingSession;
import com.voting.system.api.model.enums.VotingSessionStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    Optional<VotingSession> findById(Long id);
    
    Page<VotingSession> findAll(Pageable pageable);
    
    Page<VotingSession> findByStatus(VotingSessionStatusEnum status, Pageable pageable);
    
    Page<VotingSession> findByAgendaId(Long agendaId, Pageable pageable);

    List<VotingSession> findByAgendaId(Long agendaId);

    List<VotingSession> findByStatus(VotingSessionStatusEnum status);

    List<VotingSession> findByAgendaIdAndStatus(Long agendaId, VotingSessionStatusEnum status);

    Optional<VotingSession> findByAgendaIdAndStatusIn(Long agendaId, List<VotingSessionStatusEnum> statuses);
    
    boolean existsByAgendaIdAndStatusIn(Long agendaId, VotingSessionStatusEnum... statuses);

    @Query("SELECT vs FROM VotingSession vs WHERE vs.status = 'ACTIVE' AND vs.endTime < :currentTime")
    List<VotingSession> findExpiredSessions(@Param("currentTime") OffsetDateTime currentTime);

    @Query("SELECT vs FROM VotingSession vs WHERE vs.agenda.id = :agendaId AND vs.status IN ('ACTIVE', 'CLOSED')")
    List<VotingSession> findActiveSessionsByAgendaId(@Param("agendaId") Long agendaId);

    @Query("SELECT COUNT(vs) FROM VotingSession vs WHERE vs.agenda.id = :agendaId")
    long countByAgendaId(@Param("agendaId") Long agendaId);

    boolean existsByAgendaIdAndStatus(Long agendaId, VotingSessionStatusEnum status);
}
