package com.voting.system.api.repository;

import com.voting.system.api.model.entity.Associate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssociateRepository extends JpaRepository<Associate, Long> {

    List<Associate> findByIsActiveTrue();
    
    Page<Associate> findByIsActiveTrue(Pageable pageable);

    Optional<Associate> findByIdAndIsActiveTrue(Long id);

    Optional<Associate> findByCpf(String cpf);

    Optional<Associate> findByCpfAndIsActiveTrue(String cpf);

    boolean existsByCpf(String cpf);
    
    boolean existsByCpfAndIsActiveTrue(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    @Query("SELECT a FROM Associate a WHERE a.isActive = true AND LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Associate> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name);
    
    @Query("SELECT a FROM Associate a WHERE a.isActive = true AND LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Associate> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Associate a WHERE a.isActive = true")
    long countActiveAssociates();
}
