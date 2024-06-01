package org.example.apiapplication.services.interfaces;

import java.io.IOException;

public interface ExtractionService {
    void startExtraction();

    void extract() throws IOException;

    void stopExtraction();
}
