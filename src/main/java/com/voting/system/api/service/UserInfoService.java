package com.voting.system.api.service;

import com.voting.system.api.client.CpfValidationClient;
import com.voting.system.api.exception.BusinessException;
import com.voting.system.api.model.dto.external.CpfValidationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final CpfValidationClient cpfValidationClient;

    @Cacheable(value = "userValidation", key = "#cpf")
    public void validateUserCpf(String cpf) {
        log.info("Starting CPF validation for: {}", cpf);
        
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new BusinessException("CPF não pode estar vazio");
        }
        
        if (!cpf.matches("\\d{11}")) {
            throw new BusinessException("CPF deve conter exatamente 11 dígitos numéricos");
        }
        
        try {
            CpfValidationResponseDTO validationResponse = cpfValidationClient.validateCpf(cpf);
            
            if (validationResponse == null) {
                throw new BusinessException("Erro na validação do CPF - serviço indisponível");
            }
            
            if (!validationResponse.getValid()) {
                throw new BusinessException("CPF inválido: " + validationResponse.getMessage());
            }
            
            log.info("CPF {} validated successfully", cpf);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error validating CPF {}: {}", cpf, e.getMessage());
            log.warn("Allowing registration due to service unavailability");
        }
    }
    
    @Cacheable(value = "cpfInfo", key = "#cpf")
    public CpfValidationResponseDTO getCpfInfo(String cpf) {
        return cpfValidationClient.validateCpf(cpf);
    }
}
