package com.voting.system.api.model.entity;

import com.voting.system.api.constants.TableConstants;
import com.voting.system.api.model.enums.VotingSessionStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = TableConstants.TABLE_VOTING_SESSION)
public class VotingSession implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_agenda", referencedColumnName = "id", nullable = false)
    private Agenda agenda;

    @Column(name = "dt_start")
    private OffsetDateTime startTime;

    @Column(name = "dt_end")
    private OffsetDateTime endTime;

    @Column(name = "vl_duration_minutes", nullable = false)
    private Integer durationMinutes = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "ck_status", length = 20, nullable = false)
    private VotingSessionStatusEnum status = VotingSessionStatusEnum.PENDING;

    @CreationTimestamp
    @Column(name = "dt_created", nullable = false, updatable = false)
    private OffsetDateTime dtCreated;

    @OneToMany(mappedBy = "votingSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes = new ArrayList<>();
}
