package com.voting.system.api.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voting.system.api.constants.OpenAPIConstants;
import com.voting.system.api.model.entity.VoteValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class VoteResponseDTO {

    @JsonProperty("id")
    @Schema(description = OpenAPIConstants.ID_VOTE_TITLE, example = "1")
    private Long id;
    
    @JsonProperty(OpenAPIConstants.ID_VOTING_SESSION)
    @Schema(description = "ID da sessão de votação", example = "1")
    private Long votingSessionId;
    
    @JsonProperty(OpenAPIConstants.ID_ASSOCIATE)
    @Schema(description = "ID do associado que votou", example = "1")
    private Long associateId;
    
    @Schema(description = "Nome do associado que votou", example = "João da Silva")
    private String associateName;
    
    @JsonProperty(OpenAPIConstants.VOTE_VALUE)
    @Schema(description = OpenAPIConstants.VOTE_VALUE_TITLE, example = "YES")
    private VoteValue value;
    
    @Schema(description = "Data e hora do voto", example = "2025-09-09T10:15:30-03:00")
    private OffsetDateTime voteTime;
}
