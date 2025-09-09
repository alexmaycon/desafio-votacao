package com.voting.system.api.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voting.system.api.constants.OpenAPIConstants;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.model.validation.IUpdateValidationGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AgendaRequestDTO {

    @JsonProperty("id")
    @Schema(description = OpenAPIConstants.ID_AGENDA_TITLE, example = "1")
    @NotNull(groups = IUpdateValidationGroup.class, message = "O ID é obrigatório para atualização")
    private Long id;

    @JsonProperty(OpenAPIConstants.TITLE_AGENDA)
    @Schema(description = OpenAPIConstants.TITLE_AGENDA_TITLE, example = "Aprovação do orçamento 2025")
    @NotBlank(groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class}, 
              message = "O título da pauta é obrigatório")
    @Size(max = 255, groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class},
          message = "O título não pode ter mais de 255 caracteres")
    private String title;

    @JsonProperty(OpenAPIConstants.DESCRIPTION_AGENDA)
    @Schema(description = OpenAPIConstants.DESCRIPTION_AGENDA_TITLE, example = "Discussão sobre a aprovação do orçamento para o próximo ano")
    @Size(max = 1000, groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class},
          message = "A descrição não pode ter mais de 1000 caracteres")
    private String description;
}
