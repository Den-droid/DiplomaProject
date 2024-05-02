package org.example.apiapplication.services.implementations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.apiapplication.entities.*;
import org.example.apiapplication.enums.ScientometricSystemName;
import org.example.apiapplication.helpers.ExcelHelper;
import org.example.apiapplication.repositories.*;
import org.example.apiapplication.services.interfaces.ScientistScholarImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ScientistScholarImportExcelService implements ScientistScholarImportService {
    private final ChairRepository chairRepository;
    private final FacultyRepository facultyRepository;
    private final ScientometricSystemRepository scientometricSystemRepository;
    private final ProfileRepository profileRepository;
    private final ScientistRepository scientistRepository;

    private final ExcelHelper excelHelper;
    private boolean isImported;

    @Value("${scholar.scientist_scholar_source}")
    private String scientistScholarFilename;

    @Value("${scholar.chair_prefix}")
    private String chairPrefix;

    @Value("${scholar.faculty_prefix}")
    private String facultyPrefix;

    public ScientistScholarImportExcelService(ChairRepository chairRepository, FacultyRepository facultyRepository,
                                              ScientometricSystemRepository scientometricSystemRepository,
                                              ProfileRepository profileRepository,
                                              ScientistRepository scientistRepository,
                                              ExcelHelper excelHelper) {
        this.chairRepository = chairRepository;
        this.facultyRepository = facultyRepository;
        this.scientometricSystemRepository = scientometricSystemRepository;
        this.profileRepository = profileRepository;
        this.scientistRepository = scientistRepository;

        this.excelHelper = excelHelper;
        this.isImported = false;
    }

    @Override
    public void importFromFile() throws IOException {
        if (!isImported) {
            List<List<String>> data = excelHelper.getAll(scientistScholarFilename);

            List<Scientist> scientists = new ArrayList<>();
            List<Profile> profiles = new ArrayList<>();

            ScientometricSystem scientometricSystem = scientometricSystemRepository
                    .findByName(ScientometricSystemName.SCHOLAR)
                    .orElseThrow(() -> new EntityNotFoundException("ScientometricSystem " + ScientometricSystemName.SCHOLAR.name()));

            for (int i = 1; i < data.size(); i++) {
                if (data.get(i).get(0).equals("2408")) {
                    break;
                } else {
                    List<String> row = data.get(i);

                    List<Chair> chairsFound;
                    Chair chair = null;
                    Faculty faculty = null;
                    Scientist scientist = new Scientist();
                    Profile profile = new Profile();

                    if (!row.get(1).isEmpty()) {
                        String searchQuery = chairPrefix + " " + row.get(1);

                        chairsFound = chairRepository
                                .findByUkrainianNameContainsIgnoreCase(searchQuery);
                        if (chairsFound.size() > 1) {
                            String finalSearchQuery = searchQuery + " ";

                            chair = chairsFound.stream()
                                    .filter(ch -> (finalSearchQuery + ch.getFaculty().getUkrainianAbbreviation())
                                            .equalsIgnoreCase(ch.getUkrainianName()))
                                    .findFirst()
                                    .orElseThrow(() -> new NoSuchElementException(finalSearchQuery
                                            + chairsFound.get(0).getFaculty().getUkrainianAbbreviation()));

                            profile.setDeactivated(false);
                        } else if (chairsFound.size() == 1) {
                            chair = chairsFound.get(0);
                            profile.setDeactivated(false);
                        } else {
                            // Either Faculty name or not existing chair/faculty name is written in Excel as chair
                            searchQuery = row.get(1);

                            List<Faculty> faculties = facultyRepository
                                    .findByUkrainianNameContainsIgnoreCase(searchQuery);

                            if (faculties.size() == 1) {
                                faculty = faculties.get(0);
                                profile.setDeactivated(false);
                            } else {
                                profile.setDeactivated(true);
                            }
                        }
                    }

                    scientist.setFullName(row.get(0));
                    scientist.setPosition(row.get(3));

                    if (chair != null) {
                        scientist.setChair(chair);
                    } else if (faculty != null) {
                        scientist.setFaculty(faculty);
                    }

                    scientists.add(scientist);

                    profile.setScientometricSystem(scientometricSystem);
                    profile.setProfileUserId(row.get(4));
                    profile.setScientist(scientist);
                    profile.setAreWorksDoubtful(false);

                    profiles.add(profile);
                }
            }

            scientistRepository.saveAll(scientists);
            profileRepository.saveAll(profiles);

            isImported = true;
        }
    }
}
