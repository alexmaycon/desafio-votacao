package com.voting.system.api.service;

import com.voting.system.api.exception.DuplicateResourceException;
import com.voting.system.api.exception.ResourceNotFoundException;
import com.voting.system.api.model.dto.request.AssociateRequestDTO;
import com.voting.system.api.model.dto.response.AssociateResponseDTO;
import com.voting.system.api.model.entity.Associate;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.model.validation.IUpdateValidationGroup;
import com.voting.system.api.repository.AssociateRepository;
import com.voting.system.api.service.validator.GenericValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssociateServiceTest {

    @Mock
    private AssociateRepository associateRepository;

    @Mock
    private GenericValidator genericValidator;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserInfoService userInfoService;

    @InjectMocks
    private AssociateService associateService;

    private Associate associate;
    private AssociateRequestDTO associateRequestDTO;
    private AssociateResponseDTO associateResponseDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        associate = new Associate();
        associate.setId(1L);
        associate.setName("John Doe");
        associate.setCpf("12345678901");
        associate.setIsActive(true);
        associate.setDtCreated(OffsetDateTime.now());

        associateRequestDTO = new AssociateRequestDTO();
        associateRequestDTO.setName("John Doe");
        associateRequestDTO.setCpf("12345678901");

        associateResponseDTO = new AssociateResponseDTO();
        associateResponseDTO.setId(1L);
        associateResponseDTO.setName("John Doe");
        associateResponseDTO.setCpf("12345678901");

        pageable = PageRequest.of(0, 20);
    }

    @Test
    void create_ShouldCreateAssociateSuccessfully() {
        doNothing().when(genericValidator).validate(associateRequestDTO, ICreateValidationGroup.class);
        doNothing().when(userInfoService).validateUserCpf(associateRequestDTO.getCpf());
        when(associateRepository.existsByCpfAndIsActiveTrue(associateRequestDTO.getCpf())).thenReturn(false);
        when(modelMapper.map(associateRequestDTO, Associate.class)).thenReturn(associate);
        when(associateRepository.save(any(Associate.class))).thenReturn(associate);
        when(modelMapper.map(associate, AssociateResponseDTO.class)).thenReturn(associateResponseDTO);

        AssociateResponseDTO result = associateService.create(associateRequestDTO);

        assertNotNull(result);
        assertEquals(associateResponseDTO.getId(), result.getId());
        assertEquals(associateResponseDTO.getName(), result.getName());
        assertEquals(associateResponseDTO.getCpf(), result.getCpf());
        verify(genericValidator).validate(associateRequestDTO, ICreateValidationGroup.class);
        verify(userInfoService).validateUserCpf(associateRequestDTO.getCpf());
        verify(associateRepository).existsByCpfAndIsActiveTrue(associateRequestDTO.getCpf());
        verify(associateRepository).save(any(Associate.class));
    }

    @Test
    void create_ShouldThrowDuplicateResourceException_WhenCpfAlreadyExists() {
        doNothing().when(genericValidator).validate(associateRequestDTO, ICreateValidationGroup.class);
        doNothing().when(userInfoService).validateUserCpf(associateRequestDTO.getCpf());
        when(associateRepository.existsByCpfAndIsActiveTrue(associateRequestDTO.getCpf())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> associateService.create(associateRequestDTO));

        verify(genericValidator).validate(associateRequestDTO, ICreateValidationGroup.class);
        verify(userInfoService).validateUserCpf(associateRequestDTO.getCpf());
        verify(associateRepository).existsByCpfAndIsActiveTrue(associateRequestDTO.getCpf());
        verify(associateRepository, never()).save(any());
    }

    @Test
    void findById_ShouldReturnAssociate_WhenAssociateExists() {
        when(associateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(associate));
        when(modelMapper.map(associate, AssociateResponseDTO.class)).thenReturn(associateResponseDTO);

        AssociateResponseDTO result = associateService.findById(1L);

        assertNotNull(result);
        assertEquals(associateResponseDTO.getId(), result.getId());
        assertEquals(associateResponseDTO.getName(), result.getName());
        verify(associateRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenAssociateNotExists() {
        when(associateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> associateService.findById(1L));

        verify(associateRepository).findByIdAndIsActiveTrue(1L);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void findByCpf_ShouldReturnAssociate_WhenAssociateExists() {
        String cpf = "12345678901";
        when(associateRepository.findByCpfAndIsActiveTrue(cpf)).thenReturn(Optional.of(associate));
        when(modelMapper.map(associate, AssociateResponseDTO.class)).thenReturn(associateResponseDTO);

        AssociateResponseDTO result = associateService.findByCpf(cpf);

        assertNotNull(result);
        assertEquals(associateResponseDTO.getCpf(), result.getCpf());
        verify(associateRepository).findByCpfAndIsActiveTrue(cpf);
    }

    @Test
    void findByCpf_ShouldThrowResourceNotFoundException_WhenAssociateNotExists() {
        String cpf = "12345678901";
        when(associateRepository.findByCpfAndIsActiveTrue(cpf)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> associateService.findByCpf(cpf));

        verify(associateRepository).findByCpfAndIsActiveTrue(cpf);
    }

    @Test
    void findAll_ShouldReturnPageOfAssociates() {
        Page<Associate> associatePage = new PageImpl<>(Arrays.asList(associate));
        when(associateRepository.findByIsActiveTrue(pageable)).thenReturn(associatePage);
        when(modelMapper.map(associate, AssociateResponseDTO.class)).thenReturn(associateResponseDTO);

        Page<AssociateResponseDTO> result = associateService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(associateResponseDTO.getId(), result.getContent().get(0).getId());
        verify(associateRepository).findByIsActiveTrue(pageable);
    }

    @Test
    void update_ShouldUpdateAssociateSuccessfully() {
        doNothing().when(genericValidator).validate(associateRequestDTO, IUpdateValidationGroup.class);
        when(associateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(associate));
        when(associateRepository.save(any(Associate.class))).thenReturn(associate);
        when(modelMapper.map(associate, AssociateResponseDTO.class)).thenReturn(associateResponseDTO);

        AssociateResponseDTO result = associateService.update(1L, associateRequestDTO);

        assertNotNull(result);
        assertEquals(associateResponseDTO.getId(), result.getId());
        verify(genericValidator).validate(associateRequestDTO, IUpdateValidationGroup.class);
        verify(associateRepository).findByIdAndIsActiveTrue(1L);
        verify(associateRepository).save(any(Associate.class));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenAssociateNotExists() {
        doNothing().when(genericValidator).validate(associateRequestDTO, IUpdateValidationGroup.class);
        when(associateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> associateService.update(1L, associateRequestDTO));

        verify(associateRepository).findByIdAndIsActiveTrue(1L);
        verify(associateRepository, never()).save(any());
    }

    @Test
    void activate_ShouldActivateAssociate() {
        associate.setIsActive(false);
        when(associateRepository.findById(1L)).thenReturn(Optional.of(associate));
        when(associateRepository.save(any(Associate.class))).thenReturn(associate);

        associateService.activate(1L);

        assertTrue(associate.getIsActive());
        verify(associateRepository).findById(1L);
        verify(associateRepository).save(associate);
    }

    @Test
    void activate_ShouldThrowResourceNotFoundException_WhenAssociateNotExists() {
        when(associateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> associateService.activate(1L));

        verify(associateRepository).findById(1L);
        verify(associateRepository, never()).save(any());
    }

    @Test
    void deactivate_ShouldDeactivateAssociate() {
        when(associateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(associate));
        when(associateRepository.save(any(Associate.class))).thenReturn(associate);

        associateService.deactivate(1L);

        assertFalse(associate.getIsActive());
        verify(associateRepository).findByIdAndIsActiveTrue(1L);
        verify(associateRepository).save(associate);
    }

    @Test
    void deactivate_ShouldThrowResourceNotFoundException_WhenAssociateNotExists() {
        when(associateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> associateService.deactivate(1L));

        verify(associateRepository).findByIdAndIsActiveTrue(1L);
        verify(associateRepository, never()).save(any());
    }
}
