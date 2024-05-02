package org.example.apiapplication.exceptions.label;

public class LabelAlreadyExistsException extends RuntimeException {
    public LabelAlreadyExistsException(String label) {
        super("Таке ключове слово вже існує!");
    }
}
