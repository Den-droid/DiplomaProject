package org.example.apiapplication.exceptions.field;

public class FieldTypesNotMatchException extends RuntimeException {
    public FieldTypesNotMatchException() {
        super("Типи полів не збігаються");
    }
}
