package com.voting.system.api.exception.handler;

import com.voting.system.api.exception.BusinessException;
import com.voting.system.api.exception.DuplicateResourceException;
import com.voting.system.api.exception.ResourceNotFoundException;
import com.voting.system.api.exception.VoteException;
import com.voting.system.api.exception.VotingSessionException;
import com.voting.system.api.model.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getMessage(),
            "RESOURCE_NOT_FOUND",
            HttpStatus.NOT_FOUND.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResourceException(
            DuplicateResourceException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getMessage(),
            "DUPLICATE_RESOURCE",
            HttpStatus.CONFLICT.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler({VotingSessionException.class, VoteException.class})
    public ResponseEntity<ErrorResponseDTO> handleVotingExceptions(
            BusinessException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getMessage(),
            ex.getErrorCode(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getMessage(),
            ex.getErrorCode(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        List<String> details = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            details.add(error.getField() + ": " + error.getDefaultMessage())
        );
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            "Dados de entrada inválidos",
            "VALIDATION_ERROR",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
        error.setDetails(details);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        List<String> details = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> 
            details.add(violation.getPropertyPath() + ": " + violation.getMessage())
        );
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            "Violação de restrições de validação",
            "CONSTRAINT_VIOLATION",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
        error.setDetails(details);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            ex.getMessage(),
            "INVALID_ARGUMENT",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            "Erro interno do servidor",
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
