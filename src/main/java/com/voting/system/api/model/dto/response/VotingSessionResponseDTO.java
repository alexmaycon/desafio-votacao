package com.voting.system.api.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voting.system.api.constants.OpenAPIConstants;
import com.voting.system.api.model.enums.VotingSessionStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class VotingSessionResponseDTO {

    @JsonProperty("id")
    @Schema(description = OpenAPIConstants.ID_VOTING_SESSION_TITLE, example = "1")
    private Long id;
    
    @JsonProperty(OpenAPIConstants.ID_AGENDA)
    @Schema(description = "ID da pauta", example = "1")
    private Long agendaId;
    
    @Schema(description = "Título da pauta", example = "Aprovação do orçamento 2025")
    private String agendaTitle;
    
    @JsonProperty(OpenAPIConstants.STATUS_VOTING_SESSION)
    @Schema(description = OpenAPIConstants.STATUS_VOTING_SESSION_TITLE, example = "ACTIVE")
    private VotingSessionStatusEnum status;
    
    @Schema(description = "Data e hora de início da sessão", example = "2025-09-09T10:00:00-03:00")
    private OffsetDateTime startTime;
    
    @Schema(description = "Data e hora de término da sessão", example = "2025-09-09T10:05:00-03:00")
    private OffsetDateTime endTime;
    
    @JsonProperty(OpenAPIConstants.DURATION_MINUTES)
    @Schema(description = OpenAPIConstants.DURATION_MINUTES_TITLE, example = "5")
    private Integer durationMinutes;
    
    @JsonProperty(OpenAPIConstants.IS_ACTIVE)
    @Schema(description = OpenAPIConstants.IS_ACTIVE_TITLE, example = "true")
    private Boolean isActive;
    
    @Schema(description = "Data de criação do registro", example = "2025-09-09T09:30:00-03:00")
    private OffsetDateTime dtCreated;
    
    @Schema(description = "Data da última atualização", example = "2025-09-09T10:00:00-03:00")
    private OffsetDateTime dtUpdated;
}
