package com.voting.system.api.controller;

import com.voting.system.api.model.dto.request.VotingSessionRequestDTO;
import com.voting.system.api.model.dto.response.VotingResultDTO;
import com.voting.system.api.model.dto.response.VotingSessionResponseDTO;
import com.voting.system.api.model.enums.VotingSessionStatusEnum;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.service.interfaces.IVotingSessionService;
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
@RequestMapping("/api/v1/voting-sessions")
@RequiredArgsConstructor
@Tag(name = "Voting Session V1", description = "API V1 - Operações relacionadas às sessões de votação")
public class VotingSessionController {

    private final IVotingSessionService votingSessionService;

    @PostMapping
    @Operation(summary = "Criar nova sessão de votação", description = "Cria uma nova sessão de votação para uma pauta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Sessão criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
        @ApiResponse(responseCode = "422", description = "Já existe sessão ativa para esta pauta")
    })
    public ResponseEntity<VotingSessionResponseDTO> create(
            @RequestBody @Validated(ICreateValidationGroup.class) VotingSessionRequestDTO requestDTO) {
        
        VotingSessionResponseDTO response = votingSessionService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sessão por ID", description = "Retorna uma sessão de votação específica pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessão encontrada"),
        @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    })
    public ResponseEntity<VotingSessionResponseDTO> findById(
            @Parameter(description = "ID da sessão de votação", example = "1")
            @PathVariable Long id) {
        
        VotingSessionResponseDTO response = votingSessionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar sessões", description = "Lista todas as sessões de votação ativas com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso"),
        @ApiResponse(responseCode = "204", description = "Nenhuma sessão encontrada")
    })
    public ResponseEntity<Page<VotingSessionResponseDTO>> findAll(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        
        Page<VotingSessionResponseDTO> response = votingSessionService.findAll(pageable);
        return response.hasContent() ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @GetMapping("/agenda/{agendaId}")
    @Operation(summary = "Listar sessões por pauta", description = "Lista todas as sessões de votação de uma pauta específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessões encontradas"),
        @ApiResponse(responseCode = "204", description = "Nenhuma sessão encontrada")
    })
    public ResponseEntity<Page<VotingSessionResponseDTO>> findByAgendaId(
            @Parameter(description = "ID da pauta", example = "1")
            @PathVariable Long agendaId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        
        Page<VotingSessionResponseDTO> response = votingSessionService.findByAgendaId(agendaId, pageable);
        return response.hasContent() ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar sessões por status", description = "Lista todas as sessões com um status específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessões encontradas"),
        @ApiResponse(responseCode = "204", description = "Nenhuma sessão encontrada")
    })
    public ResponseEntity<Page<VotingSessionResponseDTO>> findByStatus(
            @Parameter(description = "Status da sessão", example = "ACTIVE")
            @PathVariable VotingSessionStatusEnum status,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        
        Page<VotingSessionResponseDTO> response = votingSessionService.findByStatus(status, pageable);
        return response.hasContent() ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/start")
    @Operation(summary = "Iniciar sessão de votação", description = "Inicia uma sessão de votação pendente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessão iniciada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Sessão não encontrada"),
        @ApiResponse(responseCode = "422", description = "Sessão não pode ser iniciada (status inválido)")
    })
    public ResponseEntity<VotingSessionResponseDTO> start(
            @Parameter(description = "ID da sessão de votação", example = "1")
            @PathVariable Long id) {
        
        VotingSessionResponseDTO response = votingSessionService.start(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Fechar sessão de votação", description = "Fecha uma sessão de votação ativa manualmente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessão fechada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Sessão não encontrada"),
        @ApiResponse(responseCode = "422", description = "Sessão não pode ser fechada (status inválido)")
    })
    public ResponseEntity<VotingSessionResponseDTO> close(
            @Parameter(description = "ID da sessão de votação", example = "1")
            @PathVariable Long id) {
        
        VotingSessionResponseDTO response = votingSessionService.close(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/result")
    @Operation(summary = "Obter resultado da votação", description = "Retorna o resultado de uma sessão de votação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resultado obtido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Sessão não encontrada"),
        @ApiResponse(responseCode = "422", description = "Sessão ainda não foi iniciada")
    })
    public ResponseEntity<VotingResultDTO> getResult(
            @Parameter(description = "ID da sessão de votação", example = "1")
            @PathVariable Long id) {
        
        VotingResultDTO response = votingSessionService.getResult(id);
        return ResponseEntity.ok(response);
    }
}
