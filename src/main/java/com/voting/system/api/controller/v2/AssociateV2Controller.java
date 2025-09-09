package com.voting.system.api.controller.v2;

import com.voting.system.api.model.dto.external.CpfValidationResponseDTO;
import com.voting.system.api.model.dto.request.AssociateRequestDTO;
import com.voting.system.api.model.dto.response.AssociateResponseDTO;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.model.validation.IUpdateValidationGroup;
import com.voting.system.api.service.UserInfoService;
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
@RequestMapping("/api/v2/associates")
@RequiredArgsConstructor
@Tag(name = "Associate V2", description = "API V2 - Operações relacionadas aos associados com validação externa de CPF")
public class AssociateV2Controller {

    private final IAssociateService associateService;
    private final UserInfoService userInfoService;

    @PostMapping
    @Operation(summary = "Criar novo associado V2", 
               description = "Cria um novo associado com validação externa de CPF e cache")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Associado criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Associado com CPF já existe"),
        @ApiResponse(responseCode = "422", description = "CPF inválido no serviço externo")
    })
    public ResponseEntity<AssociateResponseDTO> create(
            @RequestBody @Validated(ICreateValidationGroup.class) AssociateRequestDTO requestDTO) {
        
        AssociateResponseDTO response = associateService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar associado por ID V2", description = "Retorna um associado específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associado encontrado"),
        @ApiResponse(responseCode = "404", description = "Associado não encontrado")
    })
    public ResponseEntity<AssociateResponseDTO> findById(
            @PathVariable @Parameter(description = "ID do associado") Long id) {
        
        AssociateResponseDTO response = associateService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar associados V2", description = "Lista todos os associados ativos com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de associados retornada com sucesso"),
        @ApiResponse(responseCode = "204", description = "Nenhum associado encontrado")
    })
    public ResponseEntity<Page<AssociateResponseDTO>> findAll(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        
        Page<AssociateResponseDTO> response = associateService.findAll(pageable);
        
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar associado por CPF V2", description = "Retorna um associado específico pelo seu CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associado encontrado"),
        @ApiResponse(responseCode = "404", description = "Associado não encontrado")
    })
    public ResponseEntity<AssociateResponseDTO> findByCpf(
            @PathVariable @Parameter(description = "CPF do associado") String cpf) {
        
        AssociateResponseDTO response = associateService.findByCpf(cpf);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf/{cpf}/validate")
    @Operation(summary = "Validar CPF V2", 
               description = "Valida CPF através de serviço externo (NOVA FUNCIONALIDADE V2)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações do CPF retornadas"),
        @ApiResponse(responseCode = "400", description = "CPF inválido")
    })
    public ResponseEntity<CpfValidationResponseDTO> validateCpf(
            @PathVariable @Parameter(description = "CPF a ser validado") String cpf) {
        
        CpfValidationResponseDTO response = userInfoService.getCpfInfo(cpf);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar associado V2", description = "Atualiza completamente um associado existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Associado atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Associado não encontrado"),
        @ApiResponse(responseCode = "409", description = "CPF já utilizado por outro associado")
    })
    public ResponseEntity<AssociateResponseDTO> update(
            @PathVariable @Parameter(description = "ID do associado") Long id,
            @RequestBody @Validated(IUpdateValidationGroup.class) AssociateRequestDTO requestDTO) {
        
        requestDTO.setId(id);
        AssociateResponseDTO response = associateService.update(id, requestDTO);
        return ResponseEntity.ok(response);
    }
}
