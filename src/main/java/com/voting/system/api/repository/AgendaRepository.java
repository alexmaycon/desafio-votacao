package com.voting.system.api.repository;

import com.voting.system.api.model.entity.Agenda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByIsActiveTrue();
    
    Page<Agenda> findByIsActiveTrue(Pageable pageable);

    Optional<Agenda> findByIdAndIsActiveTrue(Long id);
    
    boolean existsByTitleIgnoreCaseAndIsActiveTrue(String title);

    @Query("SELECT a FROM Agenda a WHERE a.isActive = true AND LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Agenda> findByTitleContainingIgnoreCaseAndIsActiveTrue(@Param("title") String title);
    
    @Query("SELECT a FROM Agenda a WHERE a.isActive = true AND LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Agenda> findByTitleContainingIgnoreCaseAndIsActiveTrue(@Param("title") String title, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Agenda a WHERE a.isActive = true")
    long countActiveAgendas();
}
