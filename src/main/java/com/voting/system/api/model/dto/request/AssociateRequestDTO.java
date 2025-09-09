package com.voting.system.api.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voting.system.api.constants.OpenAPIConstants;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.model.validation.IUpdateValidationGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AssociateRequestDTO {

    @JsonProperty("id")
    @Schema(description = OpenAPIConstants.ID_ASSOCIATE_TITLE, example = "1")
    @NotNull(groups = IUpdateValidationGroup.class, message = "O ID é obrigatório para atualização")
    private Long id;

    @JsonProperty(OpenAPIConstants.NAME_ASSOCIATE)
    @Schema(description = OpenAPIConstants.NAME_ASSOCIATE_TITLE, example = "João da Silva")
    @NotBlank(groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class}, 
              message = "O nome do associado é obrigatório")
    @Size(max = 255, groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class},
          message = "O nome não pode ter mais de 255 caracteres")
    private String name;

    @JsonProperty(OpenAPIConstants.CPF_ASSOCIATE)
    @Schema(description = OpenAPIConstants.CPF_ASSOCIATE_TITLE, example = "12345678901")
    @NotBlank(groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class}, 
              message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class},
             message = "O CPF deve conter exatamente 11 dígitos numéricos")
    private String cpf;
}
