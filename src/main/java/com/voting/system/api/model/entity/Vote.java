package com.voting.system.api.model.entity;

import com.voting.system.api.constants.TableConstants;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = TableConstants.TABLE_VOTE,
       uniqueConstraints = @UniqueConstraint(
           name = "unique_vote_per_session",
           columnNames = {"id_voting_session", "id_associate"}
       ))
public class Vote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_voting_session", referencedColumnName = "id", nullable = false)
    private VotingSession votingSession;

    @ManyToOne
    @JoinColumn(name = "id_associate", referencedColumnName = "id", nullable = false)
    private Associate associate;

    @Enumerated(EnumType.STRING)
    @Column(name = "ck_vote_value", length = 10, nullable = false)
    private VoteValue value;

    @CreationTimestamp
    @Column(name = "dt_vote_time", nullable = false, updatable = false)
    private OffsetDateTime voteTime;
}
