package com.voting.system.api.model.entity;

import com.voting.system.api.constants.TableConstants;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = TableConstants.TABLE_AGENDA)
public class Agenda extends SoftDelete {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tx_title", length = 255, nullable = false)
    private String title;

    @Column(name = "tx_description", length = 1000)
    private String description;

    @CreationTimestamp
    @Column(name = "dt_created", nullable = false, updatable = false)
    private OffsetDateTime dtCreated;

    @UpdateTimestamp
    @Column(name = "dt_updated")
    private OffsetDateTime dtUpdated;

    @OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VotingSession> votingSessions = new ArrayList<>();
}
