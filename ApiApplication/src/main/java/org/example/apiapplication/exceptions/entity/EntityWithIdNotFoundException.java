package org.example.apiapplication.exceptions.entity;

public class EntityWithIdNotFoundException extends RuntimeException {
    public EntityWithIdNotFoundException(String entity, int id) {
        super("Сутності " + entity + " з ідентифікатором " + id + " не існує!");
    }
}
