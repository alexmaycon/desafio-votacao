package com.voting.system.api.service.interfaces;

import com.voting.system.api.model.dto.request.AssociateRequestDTO;
import com.voting.system.api.model.dto.response.AssociateResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAssociateService {

    AssociateResponseDTO create(AssociateRequestDTO requestDTO);
    
    AssociateResponseDTO findById(Long id);
    
    Page<AssociateResponseDTO> findAll(Pageable pageable);
    
    Page<AssociateResponseDTO> findByName(String name, Pageable pageable);
    
    AssociateResponseDTO findByCpf(String cpf);
    
    AssociateResponseDTO update(Long id, AssociateRequestDTO requestDTO);
    
    void activate(Long id);
    
    void deactivate(Long id);
}
