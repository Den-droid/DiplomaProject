package org.example.apiapplication.exceptions.entity;

public class EntityWithIdNotExistsException extends RuntimeException {
    public EntityWithIdNotExistsException(String entity, int id) {
        super("Entity " + entity + " with id " + id + " does not exist!");
    }
}
