package com.voting.system.api.controller;

import com.voting.system.api.model.dto.request.AssociateRequestDTO;
import com.voting.system.api.model.dto.response.AssociateResponseDTO;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.model.validation.IUpdateValidationGroup;
import com.voting.system.api.service.interfaces.IAssociateService;
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
@RequestMapping("/api/v1/associates")
@RequiredArgsConstructor
@Tag(name = "Associate V1", description = "API V1 - Operações relacionadas aos associados")
public class AssociateController {

    private final IAssociateService associateService;

    @PostMapping
    @Operation(summary = "Criar novo associado", description = "Cria um novo associado no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Associado criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "CPF já existe")
    })
    public ResponseEntity<AssociateResponseDTO> create(
            @RequestBody @Validated(ICreateValidationGroup.class) AssociateRequestDTO requestDTO) {
        
        AssociateResponseDTO response = associateService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar associado por ID", description = "Retorna um associado específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associado encontrado"),
        @ApiResponse(responseCode = "404", description = "Associado não encontrado")
    })
    public ResponseEntity<AssociateResponseDTO> findById(
            @Parameter(description = "ID do associado", example = "1")
            @PathVariable Long id) {
        
        AssociateResponseDTO response = associateService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar associados", description = "Lista todos os associados ativos com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de associados retornada com sucesso"),
        @ApiResponse(responseCode = "204", description = "Nenhum associado encontrado")
    })
    public ResponseEntity<Page<AssociateResponseDTO>> findAll(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        
        Page<AssociateResponseDTO> response = associateService.findAll(pageable);
        return response.hasContent() ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar associados por nome", description = "Busca associados que contenham o nome especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associados encontrados"),
        @ApiResponse(responseCode = "204", description = "Nenhum associado encontrado")
    })
    public ResponseEntity<Page<AssociateResponseDTO>> findByName(
            @Parameter(description = "Nome do associado para busca", example = "João")
            @RequestParam String name,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        
        Page<AssociateResponseDTO> response = associateService.findByName(name, pageable);
        return response.hasContent() ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar associado por CPF", description = "Retorna um associado específico pelo seu CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associado encontrado"),
        @ApiResponse(responseCode = "404", description = "Associado não encontrado")
    })
    public ResponseEntity<AssociateResponseDTO> findByCpf(
            @Parameter(description = "CPF do associado", example = "12345678901")
            @PathVariable String cpf) {
        
        AssociateResponseDTO response = associateService.findByCpf(cpf);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar associado", description = "Atualiza um associado existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associado atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Associado não encontrado"),
        @ApiResponse(responseCode = "409", description = "CPF já existe")
    })
    public ResponseEntity<AssociateResponseDTO> update(
            @Parameter(description = "ID do associado", example = "1")
            @PathVariable Long id,
            @RequestBody @Validated(IUpdateValidationGroup.class) AssociateRequestDTO requestDTO) {
        
        AssociateResponseDTO response = associateService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Ativar associado", description = "Ativa um associado desativado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Associado ativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associado não encontrado")
    })
    public ResponseEntity<Void> activate(
            @Parameter(description = "ID do associado", example = "1")
            @PathVariable Long id) {
        
        associateService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desativar associado", description = "Desativa um associado (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Associado desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Associado não encontrado")
    })
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "ID do associado", example = "1")
            @PathVariable Long id) {
        
        associateService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
