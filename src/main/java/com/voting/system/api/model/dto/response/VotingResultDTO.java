package com.voting.system.api.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.voting.system.api.constants.OpenAPIConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotingResultDTO {

    @JsonProperty(OpenAPIConstants.ID_AGENDA)
    @Schema(description = "ID da pauta votada", example = "1")
    private Long agendaId;
    
    @Schema(description = "Título da pauta votada", example = "Aprovação do orçamento 2025")
    private String agendaTitle;
    
    @JsonProperty(OpenAPIConstants.ID_VOTING_SESSION)
    @Schema(description = "ID da sessão de votação", example = "1")
    private Long votingSessionId;
    
    @Schema(description = "Total de votos computados", example = "10")
    private Long totalVotes;
    
    @Schema(description = "Quantidade de votos 'SIM'", example = "7")
    private Long yesVotes;
    
    @Schema(description = "Quantidade de votos 'NÃO'", example = "3")
    private Long noVotes;
    
    @Schema(description = "Resultado final da votação", example = "APROVADA", allowableValues = {"APROVADA", "REJEITADA", "EMPATE"})
    private String result;
}
