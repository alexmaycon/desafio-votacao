package com.voting.system.api.service;

import com.voting.system.api.exception.DuplicateResourceException;
import com.voting.system.api.exception.ResourceNotFoundException;
import com.voting.system.api.model.dto.request.AssociateRequestDTO;
import com.voting.system.api.model.dto.response.AssociateResponseDTO;
import com.voting.system.api.model.entity.Associate;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.model.validation.IUpdateValidationGroup;
import com.voting.system.api.repository.AssociateRepository;
import com.voting.system.api.service.interfaces.IAssociateService;
import com.voting.system.api.service.validator.GenericValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssociateService implements IAssociateService {

    private final AssociateRepository associateRepository;
    private final GenericValidator genericValidator;
    private final ModelMapper modelMapper;
    private final UserInfoService userInfoService;

    @Override
    @Transactional
    public AssociateResponseDTO create(AssociateRequestDTO requestDTO) {
        genericValidator.validate(requestDTO, ICreateValidationGroup.class);
        
        userInfoService.validateUserCpf(requestDTO.getCpf());
        
        if (associateRepository.existsByCpfAndIsActiveTrue(requestDTO.getCpf())) {
            throw new DuplicateResourceException("Associado", "CPF", requestDTO.getCpf());
        }
        
        Associate associate = modelMapper.map(requestDTO, Associate.class);
        associate.setId(null);
        associate.setIsActive(true);
        
        Associate savedAssociate = associateRepository.save(associate);
        return modelMapper.map(savedAssociate, AssociateResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public AssociateResponseDTO findById(Long id) {
        Associate associate = associateRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Associado", id));
        
        return modelMapper.map(associate, AssociateResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssociateResponseDTO> findAll(Pageable pageable) {
        Page<Associate> associates = associateRepository.findByIsActiveTrue(pageable);
        return associates.map(associate -> modelMapper.map(associate, AssociateResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssociateResponseDTO> findByName(String name, Pageable pageable) {
        Page<Associate> associates = associateRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name, pageable);
        return associates.map(associate -> modelMapper.map(associate, AssociateResponseDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public AssociateResponseDTO findByCpf(String cpf) {
        Associate associate = associateRepository.findByCpfAndIsActiveTrue(cpf)
            .orElseThrow(() -> new ResourceNotFoundException("Associado com CPF " + cpf + " não encontrado"));
        
        return modelMapper.map(associate, AssociateResponseDTO.class);
    }

    @Override
    @Transactional
    public AssociateResponseDTO update(Long id, AssociateRequestDTO requestDTO) {
        genericValidator.validate(requestDTO, IUpdateValidationGroup.class);
        
        Associate existingAssociate = associateRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Associado", id));
        
        if (!existingAssociate.getCpf().equals(requestDTO.getCpf()) &&
            associateRepository.existsByCpfAndIsActiveTrue(requestDTO.getCpf())) {
            throw new DuplicateResourceException("Associado", "CPF", requestDTO.getCpf());
        }
        
        existingAssociate.setName(requestDTO.getName());
        existingAssociate.setCpf(requestDTO.getCpf());
        
        Associate updatedAssociate = associateRepository.save(existingAssociate);
        return modelMapper.map(updatedAssociate, AssociateResponseDTO.class);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        Associate associate = associateRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Associado", id));
        
        associate.setIsActive(true);
        associateRepository.save(associate);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Associate associate = associateRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Associado", id));
        
        associate.setIsActive(false);
        associateRepository.save(associate);
    }
}
