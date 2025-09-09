package com.voting.system.api.service.interfaces;

import com.voting.system.api.model.dto.request.AgendaRequestDTO;
import com.voting.system.api.model.dto.response.AgendaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAgendaService {

    AgendaResponseDTO create(AgendaRequestDTO requestDTO);
    
    AgendaResponseDTO findById(Long id);
    
    Page<AgendaResponseDTO> findAll(Pageable pageable);
    
    Page<AgendaResponseDTO> findByTitle(String title, Pageable pageable);
    
    AgendaResponseDTO update(Long id, AgendaRequestDTO requestDTO);
    
    void activate(Long id);
    
    void deactivate(Long id);
}
