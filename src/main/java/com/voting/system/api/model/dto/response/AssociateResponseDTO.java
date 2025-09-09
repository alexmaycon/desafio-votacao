package com.voting.system.api.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voting.system.api.constants.OpenAPIConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AssociateResponseDTO {

    @JsonProperty("id")
    @Schema(description = OpenAPIConstants.ID_ASSOCIATE_TITLE, example = "1")
    private Long id;
    
    @JsonProperty(OpenAPIConstants.NAME_ASSOCIATE)
    @Schema(description = OpenAPIConstants.NAME_ASSOCIATE_TITLE, example = "João da Silva")
    private String name;
    
    @JsonProperty(OpenAPIConstants.CPF_ASSOCIATE)
    @Schema(description = OpenAPIConstants.CPF_ASSOCIATE_TITLE, example = "12345678901")
    private String cpf;
    
    @JsonProperty(OpenAPIConstants.IS_ACTIVE)
    @Schema(description = OpenAPIConstants.IS_ACTIVE_TITLE, example = "true")
    private Boolean isActive;
    
    @Schema(description = "Data de criação do registro", example = "2025-09-09T10:00:00-03:00")
    private OffsetDateTime dtCreated;
    
    @Schema(description = "Data da última atualização", example = "2025-09-09T11:30:00-03:00")
    private OffsetDateTime dtUpdated;
}
