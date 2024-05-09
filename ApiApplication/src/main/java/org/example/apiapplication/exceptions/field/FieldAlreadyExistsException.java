package org.example.apiapplication.exceptions.field;

public class FieldAlreadyExistsException extends RuntimeException{
    public FieldAlreadyExistsException(String field){
        super("Field " + field + " already exists");
    }
}
