package com.voting.system.api.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;

@Data
@MappedSuperclass
public class SoftDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "ck_active", nullable = false)
    private Boolean isActive = true;
}
