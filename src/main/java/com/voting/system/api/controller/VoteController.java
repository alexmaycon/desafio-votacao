package com.voting.system.api.controller;

import com.voting.system.api.model.dto.request.VoteRequestDTO;
import com.voting.system.api.model.dto.response.VoteResponseDTO;
import com.voting.system.api.model.dto.response.VotingResultDTO;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.service.interfaces.IVoteService;
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
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
@Tag(name = "Vote V1", description = "API V1 - Operações relacionadas aos votos")
public class VoteController {

    private final IVoteService voteService;

    @PostMapping
    @Operation(summary = "Registrar voto", description = "Registra o voto de um associado em uma sessão de votação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Sessão de votação ou associado não encontrado"),
        @ApiResponse(responseCode = "422", description = "Sessão inativa, expirada ou associado já votou")
    })
    public ResponseEntity<VoteResponseDTO> vote(
            @RequestBody @Validated(ICreateValidationGroup.class) VoteRequestDTO requestDTO) {
        
        VoteResponseDTO response = voteService.vote(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Listar votos por sessão", description = "Lista todos os votos de uma sessão de votação específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Votos encontrados"),
        @ApiResponse(responseCode = "204", description = "Nenhum voto encontrado")
    })
    public ResponseEntity<Page<VoteResponseDTO>> findByVotingSessionId(
            @Parameter(description = "ID da sessão de votação", example = "1")
            @PathVariable Long sessionId,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        
        Page<VoteResponseDTO> response = voteService.findByVotingSessionId(sessionId, pageable);
        return response.hasContent() ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @GetMapping("/session/{sessionId}/result")
    @Operation(summary = "Obter resultado detalhado da votação", description = "Retorna o resultado detalhado de uma sessão de votação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resultado obtido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Sessão de votação não encontrada")
    })
    public ResponseEntity<VotingResultDTO> getVotingResult(
            @Parameter(description = "ID da sessão de votação", example = "1")
            @PathVariable Long sessionId) {
        
        VotingResultDTO response = voteService.getVotingResult(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/associate/{associateId}/voted/{sessionId}")
    @Operation(summary = "Verificar se associado votou", description = "Verifica se um associado específico já votou em uma sessão")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificação realizada"),
        @ApiResponse(responseCode = "404", description = "Associado ou sessão não encontrado")
    })
    public ResponseEntity<Boolean> hasAssociateVoted(
            @Parameter(description = "ID do associado", example = "1")
            @PathVariable Long associateId,
            @Parameter(description = "ID da sessão de votação", example = "1")
            @PathVariable Long sessionId) {
        
        boolean hasVoted = voteService.hasAssociateVoted(sessionId, associateId);
        return ResponseEntity.ok(hasVoted);
    }
}
