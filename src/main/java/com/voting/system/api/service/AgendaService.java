package com.voting.system.api.service;

import com.voting.system.api.exception.DuplicateResourceException;
import com.voting.system.api.exception.ResourceNotFoundException;
import com.voting.system.api.model.dto.request.AgendaRequestDTO;
import com.voting.system.api.model.dto.response.AgendaResponseDTO;
import com.voting.system.api.model.entity.Agenda;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.model.validation.IUpdateValidationGroup;
import com.voting.system.api.repository.AgendaRepository;
import com.voting.system.api.service.interfaces.IAgendaService;
import com.voting.system.api.service.validator.GenericValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgendaService implements IAgendaService {

    private final AgendaRepository agendaRepository;
    private final GenericValidator genericValidator;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public AgendaResponseDTO create(AgendaRequestDTO requestDTO) {
        genericValidator.validate(requestDTO, ICreateValidationGroup.class);
        
        if (agendaRepository.existsByTitleIgnoreCaseAndIsActiveTrue(requestDTO.getTitle())) {
            throw new DuplicateResourceException("Agenda", "título", requestDTO.getTitle());
        }
        
        Agenda agenda = modelMapper.map(requestDTO, Agenda.class);
        agenda.setId(null);
        agenda.setIsActive(true);
        
        Agenda savedAgenda = agendaRepository.save(agenda);
        return modelMapper.map(savedAgenda, AgendaResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public AgendaResponseDTO findById(Long id) {
        Agenda agenda = agendaRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Agenda", id));
        
        return modelMapper.map(agenda, AgendaResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgendaResponseDTO> findAll(Pageable pageable) {
        Page<Agenda> agendas = agendaRepository.findByIsActiveTrue(pageable);
        return agendas.map(agenda -> modelMapper.map(agenda, AgendaResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgendaResponseDTO> findByTitle(String title, Pageable pageable) {
        Page<Agenda> agendas = agendaRepository.findByTitleContainingIgnoreCaseAndIsActiveTrue(title, pageable);
        return agendas.map(agenda -> modelMapper.map(agenda, AgendaResponseDTO.class));
    }

    @Override
    @Transactional
    public AgendaResponseDTO update(Long id, AgendaRequestDTO requestDTO) {
        genericValidator.validate(requestDTO, IUpdateValidationGroup.class);
        
        Agenda existingAgenda = agendaRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Agenda", id));
        
        if (!existingAgenda.getTitle().equalsIgnoreCase(requestDTO.getTitle()) &&
            agendaRepository.existsByTitleIgnoreCaseAndIsActiveTrue(requestDTO.getTitle())) {
            throw new DuplicateResourceException("Agenda", "título", requestDTO.getTitle());
        }
        
        existingAgenda.setTitle(requestDTO.getTitle());
        if (requestDTO.getDescription() != null) {
            existingAgenda.setDescription(requestDTO.getDescription());
        }
        
        Agenda updatedAgenda = agendaRepository.save(existingAgenda);
        return modelMapper.map(updatedAgenda, AgendaResponseDTO.class);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        Agenda agenda = agendaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Agenda", id));
        
        agenda.setIsActive(true);
        agendaRepository.save(agenda);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Agenda agenda = agendaRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Agenda", id));
        
        agenda.setIsActive(false);
        agendaRepository.save(agenda);
    }
}
