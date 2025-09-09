package com.voting.system.api.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voting.system.api.constants.OpenAPIConstants;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import com.voting.system.api.model.validation.IUpdateValidationGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class VotingSessionRequestDTO {

    @JsonProperty("id")
    @Schema(description = OpenAPIConstants.ID_VOTING_SESSION_TITLE, example = "1")
    @NotNull(groups = IUpdateValidationGroup.class, message = "O ID é obrigatório para atualização")
    private Long id;

    @JsonProperty(OpenAPIConstants.ID_AGENDA)
    @Schema(description = "ID da pauta para votação", example = "1")
    @NotNull(groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class}, 
             message = "O ID da pauta é obrigatório")
    private Long agendaId;

    @JsonProperty(OpenAPIConstants.DURATION_MINUTES)
    @Schema(description = OpenAPIConstants.DURATION_MINUTES_TITLE, example = "5", defaultValue = "1")
    @Positive(groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class},
              message = "A duração deve ser um valor positivo em minutos")
    private Integer durationMinutes;
}
