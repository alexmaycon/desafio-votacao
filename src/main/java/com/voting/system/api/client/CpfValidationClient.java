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

        boolean isValidCpf = isValidCpf(cpf);
        
        return new CpfValidationResponseDTO(
                cpf, 
                isValidCpf, 
                isValidCpf ? "Valid User" : null, 
                isValidCpf ? "ACTIVE" : "INVALID",
                isValidCpf ? "CPF is valid" : "CPF is invalid"
        );
    }

    private boolean isValidCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int[] digits = cpf.chars().map(c -> c - '0').toArray();

            int sum1 = 0;
            for (int i = 0; i < 9; i++) {
                sum1 += digits[i] * (10 - i);
            }
            int firstDigit = 11 - (sum1 % 11);
            if (firstDigit >= 10) {
                firstDigit = 0;
            }

            int sum2 = 0;
            for (int i = 0; i < 10; i++) {
                sum2 += digits[i] * (11 - i);
            }
            int secondDigit = 11 - (sum2 % 11);
            if (secondDigit >= 10) {
                secondDigit = 0;
            }

            return digits[9] == firstDigit && digits[10] == secondDigit;

        } catch (Exception e) {
            return false;
        }
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
