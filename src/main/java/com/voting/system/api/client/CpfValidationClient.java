package com.voting.system.api.client;

import com.voting.system.api.model.dto.external.CpfValidationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CpfValidationClient {

    private final RestTemplate restTemplate;

    @Value("${external.cpf-validation.url:https://api.validacpf.com.br}")
    private String cpfValidationBaseUrl;

    @Value("${external.cpf-validation.enabled:false}")
    private Boolean cpfValidationEnabled;

    @Cacheable(value = "cpfValidation", key = "#cpf")
    public CpfValidationResponseDTO validateCpf(String cpf) {
        log.info("Validating CPF: {}", cpf);

        if (!cpfValidationEnabled) {
            log.info("CPF validation is disabled, returning mock response");
            return createMockResponse(cpf);
        }

        try {
            String url = cpfValidationBaseUrl + "/validate/" + cpf;
            CpfValidationResponseDTO response = restTemplate.getForObject(url, CpfValidationResponseDTO.class);
            
            log.info("CPF validation response received for: {}", cpf);
            return response != null ? response : createFallbackResponse(cpf, "Empty response from service");

        } catch (RestClientException e) {
            log.warn("Error validating CPF {}: {}", cpf, e.getMessage());
            return createFallbackResponse(cpf, e.getMessage());
        }
    }

    private CpfValidationResponseDTO createMockResponse(String cpf) {
        boolean isValidFormat = cpf != null && cpf.matches("\\d{11}");
        
        if (!isValidFormat) {
            return new CpfValidationResponseDTO(cpf, false, null, "INVALID", "Invalid CPF format");
        }

        boolean isValidCpf = !cpf.equals("00000000000") && !cpf.equals("11111111111");
        
        return new CpfValidationResponseDTO(
                cpf, 
                isValidCpf, 
                isValidCpf ? "Valid User" : null, 
                isValidCpf ? "ACTIVE" : "INVALID",
                isValidCpf ? "CPF is valid" : "CPF is invalid"
        );
    }

    private CpfValidationResponseDTO createFallbackResponse(String cpf, String errorMessage) {
        return new CpfValidationResponseDTO(
                cpf, 
                true, 
                "Fallback User", 
                "UNKNOWN", 
                "Validation service unavailable, allowing registration: " + errorMessage
        );
    }
}
