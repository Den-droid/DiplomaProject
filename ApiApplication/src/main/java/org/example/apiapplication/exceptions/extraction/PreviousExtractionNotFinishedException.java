package org.example.apiapplication.exceptions.extraction;

public class PreviousExtractionNotFinishedException extends RuntimeException {
    public PreviousExtractionNotFinishedException(String system) {
        super(String.format("Попереднє витягнення з %s ще не закінчилося!", system));
    }
}
