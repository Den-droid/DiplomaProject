package org.example.apiapplication.exceptions.field;

public class FieldCannotBeDeletedException extends RuntimeException {
    public FieldCannotBeDeletedException(Integer id) {
        super("Поле з ідентифікатором " + id + " не може бути видалене!");
    }
}
