package com.voting.system.api.model.entity;

import com.voting.system.api.constants.TableConstants;
import com.voting.system.api.model.enums.AssociateStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @Transient
    public AssociateStatusEnum getStatus() {
        if (cpf == null) {
            return AssociateStatusEnum.UNABLE_TO_VOTE;
        }
        
        try {
            int randomValue = new Random().nextInt(2);
            
            return (randomValue == 0) ? 
                AssociateStatusEnum.ABLE_TO_VOTE : 
                AssociateStatusEnum.UNABLE_TO_VOTE;
        } catch (ArithmeticException e) {
            return AssociateStatusEnum.UNABLE_TO_VOTE;
        }
    }
}
