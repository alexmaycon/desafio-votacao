package com.voting.system.api.controller;

import com.voting.system.api.model.dto.request.AgendaRequestDTO;
import com.voting.system.api.model.dto.response.AgendaResponseDTO;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.model.validation.IUpdateValidationGroup;
import com.voting.system.api.service.interfaces.IAgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
@Tag(name = "Agenda V1", description = "API V1 - Operações relacionadas às pautas de votação")
public class AgendaController {

    private final IAgendaService agendaService;

    @PostMapping
    @Operation(summary = "Criar nova pauta", description = "Cria uma nova pauta para votação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Pauta com título já existe")
    })
    public ResponseEntity<AgendaResponseDTO> create(
            @RequestBody @Validated(ICreateValidationGroup.class) AgendaRequestDTO requestDTO) {
        
        AgendaResponseDTO response = agendaService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID", description = "Retorna uma pauta específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pauta encontrada"),
        @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<AgendaResponseDTO> findById(
            @Parameter(description = "ID da pauta", example = "1")
            @PathVariable Long id) {
        
        AgendaResponseDTO response = agendaService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar pautas", description = "Lista todas as pautas ativas com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pautas retornada com sucesso"),
        @ApiResponse(responseCode = "204", description = "Nenhuma pauta encontrada")
    })
    public ResponseEntity<Page<AgendaResponseDTO>> findAll(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        
        Page<AgendaResponseDTO> response = agendaService.findAll(pageable);
        return response.hasContent() ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar pautas por título", description = "Busca pautas que contenham o título especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pautas encontradas"),
        @ApiResponse(responseCode = "204", description = "Nenhuma pauta encontrada")
    })
    public ResponseEntity<Page<AgendaResponseDTO>> findByTitle(
            @Parameter(description = "Título da pauta para busca", example = "orçamento")
            @RequestParam String title,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        
        Page<AgendaResponseDTO> response = agendaService.findByTitle(title, pageable);
        return response.hasContent() ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pauta", description = "Atualiza uma pauta existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pauta atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
        @ApiResponse(responseCode = "409", description = "Título já existe em outra pauta")
    })
    public ResponseEntity<AgendaResponseDTO> update(
            @Parameter(description = "ID da pauta", example = "1")
            @PathVariable Long id,
            @RequestBody @Validated(IUpdateValidationGroup.class) AgendaRequestDTO requestDTO) {
        
        AgendaResponseDTO response = agendaService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Ativar pauta", description = "Ativa uma pauta desativada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pauta ativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<Void> activate(
            @Parameter(description = "ID da pauta", example = "1")
            @PathVariable Long id) {
        
        agendaService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desativar pauta", description = "Desativa uma pauta (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pauta desativada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "ID da pauta", example = "1")
            @PathVariable Long id) {
        
        agendaService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
