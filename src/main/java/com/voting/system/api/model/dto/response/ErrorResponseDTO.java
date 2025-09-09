package com.voting.system.api.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    private String message;
    private String errorCode;
    private int status;
    private String path;
    private OffsetDateTime timestamp;
    private List<String> details;

    public ErrorResponseDTO(String message, String errorCode, int status, String path) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.path = path;
        this.timestamp = OffsetDateTime.now();
    }

    public ErrorResponseDTO(String message, int status, String path) {
        this.message = message;
        this.errorCode = "GENERIC_ERROR";
        this.status = status;
        this.path = path;
        this.timestamp = OffsetDateTime.now();
    }
}
