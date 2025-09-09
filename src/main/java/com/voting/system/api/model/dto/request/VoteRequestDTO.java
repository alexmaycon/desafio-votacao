package com.voting.system.api.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voting.system.api.constants.OpenAPIConstants;
import com.voting.system.api.model.entity.VoteValue;
import com.voting.system.api.model.validation.ICreateValidationGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequestDTO {

    @JsonProperty(OpenAPIConstants.ID_VOTING_SESSION)
    @Schema(description = "ID da sessão de votação", example = "1")
    @NotNull(groups = ICreateValidationGroup.class, message = "O ID da sessão de votação é obrigatório")
    private Long votingSessionId;

    @JsonProperty(OpenAPIConstants.ID_ASSOCIATE)
    @Schema(description = "ID do associado que está votando", example = "1")
    @NotNull(groups = ICreateValidationGroup.class, message = "O ID do associado é obrigatório")
    private Long associateId;

    @JsonProperty(OpenAPIConstants.VOTE_VALUE)
    @Schema(description = OpenAPIConstants.VOTE_VALUE_TITLE, example = "YES", allowableValues = {"YES", "NO"})
    @NotNull(groups = ICreateValidationGroup.class, message = "O valor do voto é obrigatório")
    private VoteValue value;
}
