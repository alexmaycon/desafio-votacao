package com.voting.system.api.model.entity;

import com.voting.system.api.constants.TableConstants;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = TableConstants.TABLE_ASSOCIATE)
public class Associate extends SoftDelete {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tx_cpf", length = 11, nullable = false, unique = true)
    private String cpf;

    @Column(name = "tx_name", length = 255, nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "dt_created", nullable = false, updatable = false)
    private OffsetDateTime dtCreated;

    @OneToMany(mappedBy = "associate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes = new ArrayList<>();
}
