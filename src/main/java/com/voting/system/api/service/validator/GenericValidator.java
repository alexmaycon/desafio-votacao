package com.voting.system.api.service.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class GenericValidator {

    private final Validator validator;

    public <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getMessage()).append("; ");
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }
}
