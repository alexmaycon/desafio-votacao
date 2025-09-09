package com.voting.system.api.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voting.system.api.constants.OpenAPIConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AgendaResponseDTO {

    @JsonProperty("id")
    @Schema(description = OpenAPIConstants.ID_AGENDA_TITLE, example = "1")
    private Long id;
    
    @JsonProperty(OpenAPIConstants.TITLE_AGENDA)
    @Schema(description = OpenAPIConstants.TITLE_AGENDA_TITLE, example = "Aprovação do orçamento 2025")
    private String title;
    
    @JsonProperty(OpenAPIConstants.DESCRIPTION_AGENDA)
    @Schema(description = OpenAPIConstants.DESCRIPTION_AGENDA_TITLE, example = "Discussão sobre a aprovação do orçamento para o próximo ano")
    private String description;
    
    @JsonProperty(OpenAPIConstants.IS_ACTIVE)
    @Schema(description = OpenAPIConstants.IS_ACTIVE_TITLE, example = "true")
    private Boolean isActive;
    
    @Schema(description = "Data de criação do registro", example = "2025-09-09T10:00:00-03:00")
    private OffsetDateTime dtCreated;
    
    @Schema(description = "Data da última atualização", example = "2025-09-09T11:30:00-03:00")
    private OffsetDateTime dtUpdated;
}
