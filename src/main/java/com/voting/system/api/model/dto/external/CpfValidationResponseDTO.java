package com.voting.system.api.model.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CpfValidationResponseDTO {
    
    private String cpf;
    private Boolean valid;
    private String name;
    private String status;
    private String message;
}
