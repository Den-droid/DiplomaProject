package org.example.apiapplication.exceptions.field;

public class FieldTypesNotMatchException extends RuntimeException {
    public FieldTypesNotMatchException() {
        super("Field types not match");
    }
}
