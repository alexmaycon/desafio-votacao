package com.voting.system.api.service;

import com.voting.system.api.exception.DuplicateResourceException;
import com.voting.system.api.exception.ResourceNotFoundException;
import com.voting.system.api.model.dto.request.AgendaRequestDTO;
import com.voting.system.api.model.dto.response.AgendaResponseDTO;
import com.voting.system.api.model.entity.Agenda;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.model.validation.IUpdateValidationGroup;
import com.voting.system.api.repository.AgendaRepository;
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
class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private GenericValidator genericValidator;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AgendaService agendaService;

    private Agenda agenda;
    private AgendaRequestDTO agendaRequestDTO;
    private AgendaResponseDTO agendaResponseDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        agenda = new Agenda();
        agenda.setId(1L);
        agenda.setTitle("Test Agenda");
        agenda.setDescription("Test Description");
        agenda.setIsActive(true);
        agenda.setDtCreated(OffsetDateTime.now());

        agendaRequestDTO = new AgendaRequestDTO();
        agendaRequestDTO.setTitle("Test Agenda");
        agendaRequestDTO.setDescription("Test Description");

        agendaResponseDTO = new AgendaResponseDTO();
        agendaResponseDTO.setId(1L);
        agendaResponseDTO.setTitle("Test Agenda");
        agendaResponseDTO.setDescription("Test Description");

        pageable = PageRequest.of(0, 20);
    }

    @Test
    void create_ShouldCreateAgendaSuccessfully() {
        doNothing().when(genericValidator).validate(agendaRequestDTO, ICreateValidationGroup.class);
        when(agendaRepository.existsByTitleIgnoreCaseAndIsActiveTrue(agendaRequestDTO.getTitle())).thenReturn(false);
        when(modelMapper.map(agendaRequestDTO, Agenda.class)).thenReturn(agenda);
        when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);
        when(modelMapper.map(agenda, AgendaResponseDTO.class)).thenReturn(agendaResponseDTO);

        AgendaResponseDTO result = agendaService.create(agendaRequestDTO);

        assertNotNull(result);
        assertEquals(agendaResponseDTO.getId(), result.getId());
        assertEquals(agendaResponseDTO.getTitle(), result.getTitle());
        verify(genericValidator).validate(agendaRequestDTO, ICreateValidationGroup.class);
        verify(agendaRepository).existsByTitleIgnoreCaseAndIsActiveTrue(agendaRequestDTO.getTitle());
        verify(agendaRepository).save(any(Agenda.class));
    }

    @Test
    void create_ShouldThrowDuplicateResourceException_WhenTitleAlreadyExists() {
        doNothing().when(genericValidator).validate(agendaRequestDTO, ICreateValidationGroup.class);
        when(agendaRepository.existsByTitleIgnoreCaseAndIsActiveTrue(agendaRequestDTO.getTitle())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> agendaService.create(agendaRequestDTO));

        verify(genericValidator).validate(agendaRequestDTO, ICreateValidationGroup.class);
        verify(agendaRepository).existsByTitleIgnoreCaseAndIsActiveTrue(agendaRequestDTO.getTitle());
        verify(agendaRepository, never()).save(any());
    }

    @Test
    void findById_ShouldReturnAgenda_WhenAgendaExists() {
        when(agendaRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(agenda));
        when(modelMapper.map(agenda, AgendaResponseDTO.class)).thenReturn(agendaResponseDTO);

        AgendaResponseDTO result = agendaService.findById(1L);

        assertNotNull(result);
        assertEquals(agendaResponseDTO.getId(), result.getId());
        assertEquals(agendaResponseDTO.getTitle(), result.getTitle());
        verify(agendaRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenAgendaNotExists() {
        when(agendaRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendaService.findById(1L));

        verify(agendaRepository).findByIdAndIsActiveTrue(1L);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void findAll_ShouldReturnPageOfAgendas() {
        Page<Agenda> agendaPage = new PageImpl<>(Arrays.asList(agenda));
        when(agendaRepository.findByIsActiveTrue(pageable)).thenReturn(agendaPage);
        when(modelMapper.map(agenda, AgendaResponseDTO.class)).thenReturn(agendaResponseDTO);

        Page<AgendaResponseDTO> result = agendaService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(agendaResponseDTO.getId(), result.getContent().get(0).getId());
        verify(agendaRepository).findByIsActiveTrue(pageable);
    }

    @Test
    void findByTitle_ShouldReturnPageOfAgendas() {
        String title = "Test";
        Page<Agenda> agendaPage = new PageImpl<>(Arrays.asList(agenda));
        when(agendaRepository.findByTitleContainingIgnoreCaseAndIsActiveTrue(title, pageable)).thenReturn(agendaPage);
        when(modelMapper.map(agenda, AgendaResponseDTO.class)).thenReturn(agendaResponseDTO);

        Page<AgendaResponseDTO> result = agendaService.findByTitle(title, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(agendaRepository).findByTitleContainingIgnoreCaseAndIsActiveTrue(title, pageable);
    }

    @Test
    void update_ShouldUpdateAgendaSuccessfully() {
        doNothing().when(genericValidator).validate(agendaRequestDTO, IUpdateValidationGroup.class);
        when(agendaRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(agenda));
        when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);
        when(modelMapper.map(agenda, AgendaResponseDTO.class)).thenReturn(agendaResponseDTO);

        AgendaResponseDTO result = agendaService.update(1L, agendaRequestDTO);

        assertNotNull(result);
        assertEquals(agendaResponseDTO.getId(), result.getId());
        verify(genericValidator).validate(agendaRequestDTO, IUpdateValidationGroup.class);
        verify(agendaRepository).findByIdAndIsActiveTrue(1L);
        verify(agendaRepository).save(any(Agenda.class));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenAgendaNotExists() {
        doNothing().when(genericValidator).validate(agendaRequestDTO, IUpdateValidationGroup.class);
        when(agendaRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendaService.update(1L, agendaRequestDTO));

        verify(agendaRepository).findByIdAndIsActiveTrue(1L);
        verify(agendaRepository, never()).save(any());
    }

    @Test
    void activate_ShouldActivateAgenda() {
        agenda.setIsActive(false);
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);

        agendaService.activate(1L);

        assertTrue(agenda.getIsActive());
        verify(agendaRepository).findById(1L);
        verify(agendaRepository).save(agenda);
    }

    @Test
    void activate_ShouldThrowResourceNotFoundException_WhenAgendaNotExists() {
        when(agendaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendaService.activate(1L));

        verify(agendaRepository).findById(1L);
        verify(agendaRepository, never()).save(any());
    }

    @Test
    void deactivate_ShouldDeactivateAgenda() {
        when(agendaRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(agenda));
        when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);

        agendaService.deactivate(1L);

        assertFalse(agenda.getIsActive());
        verify(agendaRepository).findByIdAndIsActiveTrue(1L);
        verify(agendaRepository).save(agenda);
    }

    @Test
    void deactivate_ShouldThrowResourceNotFoundException_WhenAgendaNotExists() {
        when(agendaRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> agendaService.deactivate(1L));

        verify(agendaRepository).findByIdAndIsActiveTrue(1L);
        verify(agendaRepository, never()).save(any());
    }
}
