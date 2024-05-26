package org.example.apiapplication.exceptions.label;

public class LabelAlreadyExistsException extends RuntimeException {
    public LabelAlreadyExistsException(String label) {
        super("Ключове слово " + label + " вже існує!");
    }
}
