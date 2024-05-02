package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.entities.Chair;
import org.example.apiapplication.entities.Faculty;
import org.example.apiapplication.helpers.ExcelHelper;
import org.example.apiapplication.repositories.ChairRepository;
import org.example.apiapplication.repositories.FacultyRepository;
import org.example.apiapplication.services.interfaces.FacultyImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FacultyImportExcelService implements FacultyImportService {
    private final ChairRepository chairRepository;
    private final FacultyRepository facultyRepository;
    private final ExcelHelper excelHelper;
    private boolean isImported;

    @Value("${scholar.faculty_source}")
    private String facultiesFilename;

    public FacultyImportExcelService(ChairRepository chairRepository, FacultyRepository facultyRepository, ExcelHelper excelHelper) {
        this.chairRepository = chairRepository;
        this.facultyRepository = facultyRepository;
        this.excelHelper = excelHelper;
        this.isImported = false;
    }

    @Override
    public void importFromFile() throws IOException {
        if (!isImported) {
            List<List<String>> data = excelHelper.getAll(facultiesFilename);

            List<Faculty> faculties = new ArrayList<>();
            List<Chair> chairs = new ArrayList<>();

            for (int i = 2; i < data.size(); i++) {
                if (data.get(i).get(4).equalsIgnoreCase("f")) {
                    Faculty faculty = new Faculty();
                    faculty.setUkrainianName(data.get(i).get(5));
                    faculty.setUkrainianAbbreviation(data.get(i).get(6));
                    faculty.setEnglishName(data.get(i).get(7));
                    faculty.setEnglishAbbreviation(data.get(i).get(8));

                    faculties.add(faculty);
                } else if (data.get(i).get(4).equalsIgnoreCase("k") ||
                        data.get(i).get(4).equalsIgnoreCase("ko")) {
                    Chair chair = new Chair();
                    chair.setUkrainianName(data.get(i).get(5));
                    chair.setUkrainianAbbreviation(data.get(i).get(6));
                    chair.setEnglishName(data.get(i).get(7));
                    chair.setEnglishAbbreviation(data.get(i).get(8));
                    chair.setFaculty(faculties.get(faculties.size() - 1));

                    chairs.add(chair);
                } else if (data.get(i).get(4).equalsIgnoreCase("o")) {
                    break;
                }
            }

            facultyRepository.saveAll(faculties);
            chairRepository.saveAll(chairs);

            isImported = true;
        }
    }
}
