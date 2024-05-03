package org.example.apiapplication.config;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.example.apiapplication.repositories.ChairRepository;
import org.example.apiapplication.repositories.ScientistRepository;
import org.example.apiapplication.services.interfaces.ImportService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InitialConfig {
    private final ImportService importService;
    private final ChairRepository chairRepository;
    private final ScientistRepository scientistRepository;

    InitialConfig(ImportService importService,
                  ChairRepository chairRepository,
                  ScientistRepository scientistRepository) {
        this.importService = importService;
        this.chairRepository = chairRepository;
        this.scientistRepository = scientistRepository;
    }

    @PostConstruct
    @Transactional
    public void importData() throws IOException {
        if (chairRepository.count() == 0) {
            importService.importFaculties();
        }

        if (scientistRepository.count() == 0) {
            importService.importScientists();
        }
    }
}
