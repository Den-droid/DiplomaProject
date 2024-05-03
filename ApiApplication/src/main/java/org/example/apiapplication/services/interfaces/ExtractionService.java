package org.example.apiapplication.services.interfaces;

public interface ExtractionService {
    void startExtraction();

    void extract() throws Exception;

    void stopExtraction();
}
