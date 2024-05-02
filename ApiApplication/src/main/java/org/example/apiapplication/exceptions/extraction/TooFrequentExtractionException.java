package org.example.apiapplication.exceptions.extraction;

import java.time.LocalDate;

public class TooFrequentExtractionException extends RuntimeException {
    public TooFrequentExtractionException(String system, LocalDate nextExtractionDate) {
        super(String.format("Ви занадто часто намагаєтеся витягувати дані з %s!!! Наступне витягнення " +
                "зможе бути виконане мінімум в %s", system, nextExtractionDate.toString()));
    }
}
