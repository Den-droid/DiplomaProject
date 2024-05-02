package org.example.apiapplication.exceptions.entity;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity, String entityName) {
        super(String.format("%s %s not found!", entity, entityName));
    }
}
