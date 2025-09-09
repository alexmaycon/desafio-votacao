package com.voting.system.api.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final Object resourceId;

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s não encontrado com ID: %s", resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = "Resource";
        this.resourceId = null;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Object getResourceId() {
        return resourceId;
    }
}
